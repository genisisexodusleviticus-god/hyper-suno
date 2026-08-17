package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricPurple,
    onPrimary = VoidBlack,
    primaryContainer = SurfaceHighlight,
    onPrimaryContainer = NeonViolet,
    secondary = AcidGreen,
    onSecondary = VoidBlack,
    secondaryContainer = SurfaceContainer,
    onSecondaryContainer = ToxicGreen,
    tertiary = CyberTurquoise,
    onTertiary = VoidBlack,
    tertiaryContainer = SurfaceHighlight,
    onTertiaryContainer = AngelAqua,
    background = VoidBlack,
    onBackground = NeonWhite,
    surface = SurfaceDark,
    onSurface = NeonWhite,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = MutedSlate,
    error = BloodCrimson,
    onError = NeonWhite,
    outline = DarkPurpleBorder,
    outlineVariant = DarkBorder
)

@Composable
fun SunoHyperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
