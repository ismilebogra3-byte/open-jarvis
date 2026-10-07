package com.openjarvis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object VoidColor {
    val Void950 = Color(0xFF08080C)
    val Void900 = Color(0xFF0F0F15)
    val Void800 = Color(0xFF181820)

    val Violet = Color(0xFF8B5CF6)
    val Cyan = Color(0xFF22D3EE)

    val Green = Color(0xFF22C55E)
    val Red = Color(0xFFEF4444)
    val Amber = Color(0xFFF59E0B)

    val BorderGlow = Color(0x338B5CF6)
    val BorderSubtle = Color(0x261F2937)

    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFFA1A1AA)
    val TextDisabled = Color(0xFF71717A)
}

private val OpenJarvisDarkColors = darkColorScheme(
    primary = VoidColor.Violet,
    background = VoidColor.Void950,
    surface = VoidColor.Void950,
    onBackground = VoidColor.TextPrimary,
    onSurface = VoidColor.TextPrimary
)

@Composable
fun OpenJarvisTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OpenJarvisDarkColors,
        content = content
    )
}
