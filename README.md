<!--
  NothingBattery — offline battery monitor for Android in Nothing OS style.
  Copyright (C) 2026 Real3rr0r303YT. Made by Real3rr0r303YT.
  SPDX-License-Identifier: GPL-3.0-only
-->

# NothingBattery

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-red.svg)](LICENSE)
[![CI](https://github.com/Real3rr0r303YT/NothingBattery/actions/workflows/ci.yml/badge.svg)](https://github.com/Real3rr0r303YT/NothingBattery/actions/workflows/ci.yml)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20me%20a%20coffee-support-white?logo=buymeacoffee&logoColor=black&labelColor=white&color=D71921)](https://buymeacoffee.com/3rr0r303YT)

A minimalist, **100 % offline** battery monitor for Android in the Nothing OS design language —
deep black, pure white, red accents and dot-matrix type.

**Made by Real3rr0r303YT** · Licensed under the [GNU GPL v3.0](LICENSE)

## Features (v0.1.0)

- **Live battery monitor** (native `BatteryManager`, refreshed every second)
  - Level (%), status (charging / discharging / full) and power source (AC / USB / wireless / dock)
  - Health, temperature (°C), voltage (mV), current (mA) and power (W)
  - Charge counter (mAh), estimated full capacity, cycle count (Android 14+), technology
- **Charge-limit warning** at **80 %** or **85 %**
  - Dot ring shows the limit as a red marker; a banner appears when approaching / reaching it
  - Optional notification ("Notify me") via a tiny offline foreground service — one alert per
    charging session, re-armed after unplugging, restored after reboot
- **Dot-matrix dashboard** — hand-drawn 5×7 dot font (no bundled or downloaded fonts),
  dot ring gauge and a 60-second current graph
- **About section** with GPL-3.0 notice, built-in offline license viewer and unobtrusive
  *Buy me a coffee* / *Source code* links (opened in your browser — the app itself never
  touches the network)
- English and German UI

## Privacy / OpSec

- **No `INTERNET` permission.** The manifest explicitly removes `INTERNET` and
  `ACCESS_NETWORK_STATE` in case a library tries to merge them in, and CI fails the build if
  any network permission ends up in the APK (`scripts/check-permissions.sh`).
- No analytics, no crash reporting, no Google Play Services, no ads.
- Backups and device-to-device transfer are disabled; settings never leave the device.
- The Google dependency-info blob is stripped from the APK (`dependenciesInfo`).

Permissions used: `POST_NOTIFICATIONS`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`
(only while "Notify me" is on) and `RECEIVE_BOOT_COMPLETED` (to restore the guard after reboot).

## Requirements

- Android 8.0 (API 26) or newer; targets Android 15 (API 35)
- Building: JDK 17, Android SDK 35

## Build

```bash
./gradlew testDebugUnitTest   # unit tests
./gradlew assembleDebug       # → app/build/outputs/apk/debug/app-debug.apk
```

## CI/CD

| Workflow | Trigger | What it does |
|---|---|---|
| `ci.yml` | every push / PR | unit tests, lint, debug APK, offline-permission check, uploads APK artifact |
| `release.yml` | tag `v*` (e.g. `v0.1.0`) or manual *Run workflow* (creates the tag) | verifies tag = `versionName`, builds a **signed** release APK, verifies signature + permissions, publishes a GitHub Release with APK and SHA-256 |

### Signing

Release signing reads everything from environment variables, nothing is stored in the repo.
Create a keystore once and keep it safe (losing it means users can't update):

```bash
keytool -genkeypair -v -keystore nothingbattery-release.jks \
  -alias nothingbattery -keyalg RSA -keysize 4096 -validity 10000
base64 -w0 nothingbattery-release.jks > keystore.b64
```

Add these repository secrets (Settings → Secrets and variables → Actions):

| Secret | Value |
|---|---|
| `NB_KEYSTORE_BASE64` | contents of `keystore.b64` |
| `NB_KEYSTORE_PASSWORD` | keystore password |
| `NB_KEY_ALIAS` | `nothingbattery` (or your alias) |
| `NB_KEY_PASSWORD` | key password |

Then release with:

```bash
git tag v0.1.0 && git push origin v0.1.0
```

To sign locally, export `NB_KEYSTORE_PATH`, `NB_KEYSTORE_PASSWORD`, `NB_KEY_ALIAS` and
`NB_KEY_PASSWORD` before running `./gradlew assembleRelease`. Without them the release APK is
built unsigned.

## Project structure

```
app/src/main/java/io/github/real3rr0r303yt/nothingbattery/
├── battery/        BatteryManager reader, pure conversion math, charge-limit policy
├── settings/       SharedPreferences-backed settings (limit, alerts on/off)
├── service/        ChargeGuardService (foreground), notifications, boot receiver
└── ui/
    ├── theme/      Nothing colors & typography
    ├── components/ Dot-matrix text, dot ring, dot graph, panels, switch, segmented control
    └── dashboard/  Dashboard screen + ViewModel
```

## Notes on accuracy

`BATTERY_PROPERTY_CURRENT_NOW` is specified in µA, but some OEM kernels report mA. NothingBattery
treats values with magnitude ≥ 10 000 as µA and smaller ones as mA. Positive current means the
battery is charging. Estimated capacity is extrapolated from the charge counter and is only shown
above 20 %.

## Support

NothingBattery is free, has no ads and no tracking — and it will stay that way.
If it helps you keep your battery healthy, you can support the development:

<a href="https://buymeacoffee.com/3rr0r303YT"><img src="https://img.shields.io/badge/Buy%20me%20a%20coffee-%E2%98%95-D71921?style=for-the-badge&labelColor=000000" alt="Buy Me a Coffee"></a>

**→ [buymeacoffee.com/3rr0r303YT](https://buymeacoffee.com/3rr0r303YT)**

Starring the repo, reporting bugs and sending pull requests helps just as much.

## License

```
NothingBattery
Copyright (C) 2026 Real3rr0r303YT

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

NothingBattery is licensed under the **GNU General Public License v3.0 (GPL-3.0)** —
**Made by Real3rr0r303YT**.

- The complete, unmodified license text is in [`LICENSE`](LICENSE)
  (also available at <https://www.gnu.org/licenses/gpl-3.0.html>).
- The same text ships inside the app (`app/src/main/assets/LICENSE`) and can be read offline
  under **About → GNU GPL-3.0 license**. CI fails if the bundled copy ever differs from `LICENSE`.
- Every source file carries an `SPDX-License-Identifier: GPL-3.0-only` header.
- Forks and derivatives must stay under GPL-3.0 and keep the copyright notice.

## Disclaimer

NothingBattery is an independent open-source project and is not affiliated with or endorsed by
Nothing Technology Limited.

---

<p align="center"><b>Made by Real3rr0r303YT</b></p>
