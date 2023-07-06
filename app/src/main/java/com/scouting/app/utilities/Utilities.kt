package com.scouting.app.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.scouting.app.MainActivity

/**
 * Returns a [MutableState] that will be initialized with [value] during the first composition and when a different
 * value is passed. Useful for providing initial values to variables.
 */
@Composable
fun <T> rememberInitial(value: T) = remember(value) { mutableStateOf(value) }

val composableContext @Composable get() = LocalContext.current as MainActivity

fun Modifier.longPressEffect(offset: Float?): Modifier =
    this
        .zIndex(offset?.let { 1f } ?: 0f)
        .graphicsLayer {
            with(offset ?: 0f) { translationY = this }
            offset?.let {
                scaleX = 1.02F
                scaleY = 1.02F
            }
        }

fun NavController.returnTo(route: String) = navigate(route, navOptions { popUpTo(route) { inclusive = true } })

fun String.quoteForCSV(): String {
    val ret = replace("\"", "\"\"")
    if (ret.contains(',')) {
        return "\"$ret\""
    }
    return ret
}
