package io.github.real3rr0r303yt.nothingbattery.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingColors

/** Hand-made 5×7 dot-matrix font — no bundled font files, no network fonts. */
internal object DotFont {
    const val COLS = 5
    const val ROWS = 7

    private val glyphs: Map<Char, List<String>> = mapOf(
        '0' to listOf(".###.", "#...#", "#..##", "#.#.#", "##..#", "#...#", ".###."),
        '1' to listOf("..#..", ".##..", "..#..", "..#..", "..#..", "..#..", ".###."),
        '2' to listOf(".###.", "#...#", "....#", "...#.", "..#..", ".#...", "#####"),
        '3' to listOf("#####", "...#.", "..#..", "...#.", "....#", "#...#", ".###."),
        '4' to listOf("...#.", "..##.", ".#.#.", "#..#.", "#####", "...#.", "...#."),
        '5' to listOf("#####", "#....", "####.", "....#", "....#", "#...#", ".###."),
        '6' to listOf("..##.", ".#...", "#....", "####.", "#...#", "#...#", ".###."),
        '7' to listOf("#####", "....#", "...#.", "..#..", ".#...", ".#...", ".#..."),
        '8' to listOf(".###.", "#...#", "#...#", ".###.", "#...#", "#...#", ".###."),
        '9' to listOf(".###.", "#...#", "#...#", ".####", "....#", "...#.", ".##.."),
        '%' to listOf("##...", "##..#", "...#.", "..#..", ".#...", "#..##", "...##"),
        '°' to listOf(".##..", "#..#.", ".##..", ".....", ".....", ".....", "....."),
        '.' to listOf(".....", ".....", ".....", ".....", ".....", ".##..", ".##.."),
        '-' to listOf(".....", ".....", ".....", "#####", ".....", ".....", "....."),
        ':' to listOf(".....", ".##..", ".##..", ".....", ".##..", ".##..", "....."),
        'A' to listOf(".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
        'B' to listOf("####.", "#...#", "#...#", "####.", "#...#", "#...#", "####."),
        'C' to listOf(".###.", "#...#", "#....", "#....", "#....", "#...#", ".###."),
        'D' to listOf("###..", "#..#.", "#...#", "#...#", "#...#", "#..#.", "###.."),
        'E' to listOf("#####", "#....", "#....", "####.", "#....", "#....", "#####"),
        'F' to listOf("#####", "#....", "#....", "####.", "#....", "#....", "#...."),
        'G' to listOf(".###.", "#...#", "#....", "#.###", "#...#", "#...#", ".####"),
        'H' to listOf("#...#", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
        'I' to listOf(".###.", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."),
        'J' to listOf("..###", "...#.", "...#.", "...#.", "...#.", "#..#.", ".##.."),
        'K' to listOf("#...#", "#..#.", "#.#..", "##...", "#.#..", "#..#.", "#...#"),
        'L' to listOf("#....", "#....", "#....", "#....", "#....", "#....", "#####"),
        'M' to listOf("#...#", "##.##", "#.#.#", "#.#.#", "#...#", "#...#", "#...#"),
        'N' to listOf("#...#", "#...#", "##..#", "#.#.#", "#..##", "#...#", "#...#"),
        'O' to listOf(".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
        'P' to listOf("####.", "#...#", "#...#", "####.", "#....", "#....", "#...."),
        'Q' to listOf(".###.", "#...#", "#...#", "#...#", "#.#.#", "#..#.", ".##.#"),
        'R' to listOf("####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"),
        'S' to listOf(".####", "#....", "#....", ".###.", "....#", "....#", "####."),
        'T' to listOf("#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."),
        'U' to listOf("#...#", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
        'V' to listOf("#...#", "#...#", "#...#", "#...#", "#...#", ".#.#.", "..#.."),
        'W' to listOf("#...#", "#...#", "#...#", "#.#.#", "#.#.#", "#.#.#", ".#.#."),
        'X' to listOf("#...#", "#...#", ".#.#.", "..#..", ".#.#.", "#...#", "#...#"),
        'Y' to listOf("#...#", "#...#", ".#.#.", "..#..", "..#..", "..#..", "..#.."),
        'Z' to listOf("#####", "....#", "...#.", "..#..", ".#...", "#....", "#####"),
    )

    private val blank = List(ROWS) { "....." }

    fun glyph(c: Char): List<String> = glyphs[c.uppercaseChar()] ?: blank
}

/**
 * Renders [text] as a grid of dots. Unlit dots are drawn in [offColor] so the matrix
 * reads like a physical LED panel (pass [Color.Transparent] to hide them).
 */
@Composable
fun DotMatrixText(
    text: String,
    modifier: Modifier = Modifier,
    dotSize: Dp = 6.dp,
    gap: Dp = 2.dp,
    color: Color = NothingColors.White,
    offColor: Color = NothingColors.DotOff,
    accentIndex: Int = -1,
    accentColor: Color = NothingColors.Red,
) {
    val columns = if (text.isEmpty()) 0 else text.length * (DotFont.COLS + 1) - 1
    val pitch = dotSize + gap
    Canvas(
        modifier
            .size(width = (pitch * columns - gap).coerceAtLeast(0.dp), height = pitch * DotFont.ROWS - gap)
            .semantics { contentDescription = text },
    ) {
        val p = pitch.toPx()
        val r = dotSize.toPx() / 2f
        text.forEachIndexed { index, char ->
            val glyph = DotFont.glyph(char)
            val x0 = index * (DotFont.COLS + 1) * p
            val lit = if (index == accentIndex) accentColor else color
            for (row in 0 until DotFont.ROWS) {
                for (col in 0 until DotFont.COLS) {
                    val on = glyph[row][col] == '#'
                    if (!on && offColor.alpha == 0f) continue
                    drawCircle(
                        color = if (on) lit else offColor,
                        radius = r,
                        center = Offset(x0 + col * p + r, row * p + r),
                    )
                }
            }
        }
    }
}
