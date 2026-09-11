package com.meshtalk.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = MeshPrimary,
    onPrimary = MeshOnPrimary,
    secondary = MeshSecondary,
    background = MeshBackground,
    surface = MeshSurface,
    error = MeshError
)

private val DarkColors = darkColorScheme(
    primary = MeshPrimaryDark,
    onPrimary = MeshOnPrimaryDark,
    secondary = MeshSecondaryDark,
    background = MeshBackgroundDark,
    surface = MeshSurfaceDark,
    error = MeshErrorDark
)

/**
 * App-wide Material 3 theme. Dynamic color is intentionally not wired up yet -
 * MeshTalk ships a deliberate brand palette rather than deriving from wallpaper,
 * but the seed colors above are structured so `dynamicColorScheme` could be swapped
 * in behind a settings toggle later.
 */
@Composable
fun MeshTalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MeshTypography,
        content = content
    )
}
