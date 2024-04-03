package com.mgws.adownkyi.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val passLight = Color(0xFF1ABA1A)
val passContainerLight = Color(0xFFDAFFD6)
val warnLight = Color(0xFFDAB61A)
val warnContainerLight = Color(0xFFFFFFD6)

val passDark = Color(0xFFB4FFAB)
val passContainerDark = Color(0xFF00930A)
val warnDark = Color(0xFFFFFFAB)
val warnContainerDark = Color(0xFF93890A)

data class ColorScheme(
    val warn: Color,
    val warnContainer: Color,
    val pass: Color,
    val passContainer: Color,
)

val LightLocalColorScheme = ColorScheme(
    warn = warnLight,
    warnContainer = warnContainerLight,
    pass = passLight,
    passContainer = passContainerLight,
)

val DarkLocalColorScheme = ColorScheme(
    warn = warnDark,
    warnContainer = warnContainerDark,
    pass = passDark,
    passContainer = passContainerDark,
)


val LocalColorScheme = staticCompositionLocalOf { LightLocalColorScheme }