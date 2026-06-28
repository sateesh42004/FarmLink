package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = FarmGreenLight,
    onPrimary = Color(0xFF081C15),
    primaryContainer = FarmGreenDark,
    onPrimaryContainer = LightGreenTint,
    secondary = ClayTertiary,
    onSecondary = Color(0xFFE0E4DE),
    secondaryContainer = FarmGreenPrimary,
    onSecondaryContainer = LightGreenTint,
    tertiary = EarthAmberLight,
    onTertiary = Color(0xFF081C15),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFA4B29C),
    outline = DarkBorderColor,
    outlineVariant = DarkBorderColor,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = FarmGreenPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = LightGreenTint,
    onPrimaryContainer = Color(0xFF1B4332),
    secondary = ClayTertiary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightOnBackground,
    tertiary = EarthAmberPrimary,
    onTertiary = Color(0xFFFFFFFF),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = ClayTertiary,
    outline = BorderColor,
    outlineVariant = BorderColor,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Default dynamicColor to false to fully express the requested brand-specific design theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
