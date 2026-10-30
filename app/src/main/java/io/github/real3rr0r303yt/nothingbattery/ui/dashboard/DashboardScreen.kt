package io.github.real3rr0r303yt.nothingbattery.ui.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.real3rr0r303yt.nothingbattery.BuildConfig
import io.github.real3rr0r303yt.nothingbattery.R
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryHealth
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryState
import io.github.real3rr0r303yt.nothingbattery.battery.ChargeLimitPolicy
import io.github.real3rr0r303yt.nothingbattery.battery.ChargeStatus
import io.github.real3rr0r303yt.nothingbattery.battery.LimitIndicator
import io.github.real3rr0r303yt.nothingbattery.battery.PlugType
import io.github.real3rr0r303yt.nothingbattery.service.Notifications
import io.github.real3rr0r303yt.nothingbattery.settings.Settings
import io.github.real3rr0r303yt.nothingbattery.ui.components.DotGraph
import io.github.real3rr0r303yt.nothingbattery.ui.components.DotMatrixText
import io.github.real3rr0r303yt.nothingbattery.ui.components.DotRing
import io.github.real3rr0r303yt.nothingbattery.ui.components.Label
import io.github.real3rr0r303yt.nothingbattery.ui.components.NothingSwitch
import io.github.real3rr0r303yt.nothingbattery.ui.components.Panel
import io.github.real3rr0r303yt.nothingbattery.ui.components.PulseDot
import io.github.real3rr0r303yt.nothingbattery.ui.components.Segmented
import io.github.real3rr0r303yt.nothingbattery.ui.components.StatTile
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingBatteryTheme
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingColors
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingType
import java.util.Locale

private const val HOT_CELSIUS = 40f
private const val CRITICAL_LEVEL = 15

@Composable
fun DashboardRoute(vm: DashboardViewModel = viewModel()) {
    val battery by vm.battery.collectAsStateWithLifecycle()
    val history by vm.currentHistory.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionDenied = !granted
        if (granted) vm.setAlertsEnabled(true)
    }

    DashboardScreen(
        battery = battery,
        history = history,
        settings = settings,
        permissionDenied = permissionDenied,
        onLimitChange = vm::setChargeLimit,
        onAlertsChange = { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Notifications.canPost(context)) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                permissionDenied = false
                vm.setAlertsEnabled(enabled)
            }
        },
    )
}

@Composable
fun DashboardScreen(
    battery: BatteryState?,
    history: List<Int>,
    settings: Settings,
    permissionDenied: Boolean,
    onLimitChange: (Int) -> Unit,
    onAlertsChange: (Boolean) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(NothingColors.Black),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Header(battery)
            if (battery == null) {
                Label(stringResource(R.string.loading), Modifier.padding(vertical = 48.dp))
            } else {
                Hero(battery, settings.chargeLimit)
                LimitBanner(ChargeLimitPolicy.indicator(battery.levelPercent, battery.isPlugged, settings.chargeLimit), settings.chargeLimit)
                StatsGrid(battery)
                Panel(Modifier.fillMaxWidth()) {
                    Label(stringResource(R.string.label_current_history))
                    Spacer(Modifier.height(14.dp))
                    DotGraph(history, Modifier.fillMaxWidth().height(64.dp))
                }
            }
            ChargeLimitPanel(settings, permissionDenied, onLimitChange, onAlertsChange)
            Footer()
        }
    }
}

@Composable
private fun Header(battery: BatteryState?) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        DotMatrixText("BATTERY", dotSize = 3.dp, gap = 1.dp, offColor = Color.Transparent)
        Spacer(Modifier.weight(1f))
        if (battery != null) {
            if (battery.status == ChargeStatus.CHARGING) {
                PulseDot()
                Spacer(Modifier.size(8.dp))
            }
            val plug = if (battery.isPlugged) " · " + stringResource(battery.plug.label) else ""
            Label(stringResource(battery.status.label) + plug, color = NothingColors.White)
        }
    }
}

@Composable
private fun Hero(battery: BatteryState, limit: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        DotRing(
            levelPercent = battery.levelPercent,
            limitPercent = limit,
            charging = battery.status == ChargeStatus.CHARGING,
            critical = !battery.isPlugged && battery.levelPercent in 0..CRITICAL_LEVEL,
            modifier = Modifier.fillMaxSize(),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val text = if (battery.levelPercent < 0) "--%" else "${battery.levelPercent}%"
            DotMatrixText(text, dotSize = 5.dp, gap = 2.dp, accentIndex = text.lastIndex)
            Spacer(Modifier.height(16.dp))
            Label(stringResource(R.string.limit_title) + " · $limit %")
        }
    }
}

@Composable
private fun LimitBanner(indicator: LimitIndicator, limit: Int) {
    if (indicator == LimitIndicator.IDLE) return
    val reached = indicator == LimitIndicator.REACHED
    Panel(Modifier.fillMaxWidth(), borderColor = if (reached) NothingColors.Red else NothingColors.Outline) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulseDot(color = if (reached) NothingColors.Red else NothingColors.White)
            Spacer(Modifier.size(12.dp))
            Label(
                stringResource(if (reached) R.string.limit_reached_banner else R.string.limit_approaching_banner, limit),
                color = if (reached) NothingColors.Red else NothingColors.White,
            )
        }
    }
}

@Composable
private fun StatsGrid(b: BatteryState) {
    val na = stringResource(R.string.value_na)
    val tiles = buildList {
        add(Tile(stringResource(R.string.label_health), stringResource(b.health.label), null, b.health.isProblem))
        add(Tile(stringResource(R.string.label_temperature), fmt("%.1f", b.temperatureC), "°C", b.temperatureC >= HOT_CELSIUS))
        add(Tile(stringResource(R.string.label_voltage), b.voltageMv.toString(), "mV"))
        add(Tile(stringResource(R.string.label_current), b.currentMa?.let { fmt("%+d", it) } ?: na, "mA"))
        add(Tile(stringResource(R.string.label_power), b.powerW?.let { fmt("%.2f", it) } ?: na, "W"))
        add(Tile(stringResource(R.string.label_charge), b.chargeCounterMah?.toString() ?: na, "mAh"))
        b.estimatedCapacityMah?.let { add(Tile(stringResource(R.string.label_capacity), it.toString(), "mAh")) }
        b.cycleCount?.let { add(Tile(stringResource(R.string.label_cycles), it.toString(), null)) }
        b.technology?.let { add(Tile(stringResource(R.string.label_technology), it, null)) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tiles.chunked(2).forEach { row ->
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { t -> StatTile(t.label, t.value, t.unit, Modifier.weight(1f).fillMaxHeight(), t.alert) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private data class Tile(val label: String, val value: String, val unit: String?, val alert: Boolean = false)

@Composable
private fun ChargeLimitPanel(
    settings: Settings,
    permissionDenied: Boolean,
    onLimitChange: (Int) -> Unit,
    onAlertsChange: (Boolean) -> Unit,
) {
    Panel(Modifier.fillMaxWidth()) {
        Label(stringResource(R.string.limit_title), color = NothingColors.White)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.limit_description), color = NothingColors.Muted, style = NothingType.Body)
        Spacer(Modifier.height(16.dp))
        Segmented(
            options = ChargeLimitPolicy.SUPPORTED_LIMITS,
            selected = settings.chargeLimit,
            label = { "$it %" },
            onSelect = onLimitChange,
        )
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.limit_notify), color = NothingColors.White, style = NothingType.Body)
                Text(stringResource(R.string.limit_notify_hint), color = NothingColors.Muted, style = NothingType.Unit)
            }
            NothingSwitch(checked = settings.alertsEnabled, onCheckedChange = onAlertsChange)
        }
        if (permissionDenied) {
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.limit_permission_denied), color = NothingColors.Red, style = NothingType.Unit)
        }
    }
}

@Composable
private fun Footer() {
    Column(
        Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Label(stringResource(R.string.offline_badge))
        Label(stringResource(R.string.brand_made_by) + " · v" + BuildConfig.VERSION_NAME, color = NothingColors.White)
    }
}

private fun fmt(pattern: String, vararg args: Any): String = String.format(Locale.ROOT, pattern, *args)

@Preview(widthDp = 380, heightDp = 1400, showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun DashboardPreview() {
    NothingBatteryTheme {
        DashboardScreen(
            battery = BatteryState(
                levelPercent = 82,
                status = ChargeStatus.CHARGING,
                plug = PlugType.AC,
                health = BatteryHealth.GOOD,
                temperatureC = 31.4f,
                voltageMv = 4312,
                currentMa = 1480,
                chargeCounterMah = 3936,
                cycleCount = 214,
                technology = "Li-ion",
                timestampMs = 0L,
            ),
            history = List(60) { 900 + (it * 37) % 700 },
            settings = Settings(chargeLimit = 80, alertsEnabled = true),
            permissionDenied = false,
            onLimitChange = {},
            onAlertsChange = {},
        )
    }
}
