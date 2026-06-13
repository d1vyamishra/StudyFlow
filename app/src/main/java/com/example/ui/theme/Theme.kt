package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkBentoPrimary,
    onPrimary = DarkBentoOnPrimary,
    primaryContainer = DarkBentoPrimaryContainer,
    onPrimaryContainer = DarkBentoOnPrimaryContainer,
    secondary = DarkBentoSecondary,
    onSecondary = DarkBentoOnSecondary,
    secondaryContainer = DarkBentoSecondaryContainer,
    onSecondaryContainer = DarkBentoOnSecondaryContainer,
    tertiary = DarkBentoTertiary,
    onTertiary = DarkBentoOnTertiary,
    tertiaryContainer = DarkBentoTertiaryContainer,
    onTertiaryContainer = DarkBentoOnTertiaryContainer,
    background = DarkBentoBg,
    onBackground = DarkBentoText,
    surface = DarkBentoSurface,
    onSurface = DarkBentoOnSurface,
    outline = DarkBentoOutline,
    outlineVariant = DarkBentoOutlineVariant
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BentoPrimary,
    onPrimary = BentoOnPrimary,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    secondary = BentoSecondary,
    onSecondary = BentoOnSecondary,
    secondaryContainer = BentoSecondaryContainer,
    onSecondaryContainer = BentoOnSecondaryContainer,
    tertiary = BentoTertiary,
    onTertiary = BentoOnTertiary,
    tertiaryContainer = BentoTertiaryContainer,
    onTertiaryContainer = BentoOnTertiaryContainer,
    background = BentoBg,
    onBackground = BentoText,
    surface = BentoSurface,
    onSurface = BentoOnSurface,
    surfaceVariant = BentoSurfaceVariant,
    onSurfaceVariant = BentoOnSurfaceVariant,
    outline = BentoOutline,
    outlineVariant = BentoOutlineVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default to keep the custom colors intact
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
