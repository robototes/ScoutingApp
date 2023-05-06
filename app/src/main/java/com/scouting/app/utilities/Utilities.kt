package com.scouting.app.utilities

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.navOptions

/**
 * Returns a [MutableState] that will be initialized with [value] during the first composition and when a different
 * value is passed. Useful for providing initial values to variables.
 */
@Composable
fun <T> rememberInitial(value: T) = remember(value) { mutableStateOf(value) }

fun <T : ViewModel> Context.getViewModel(type: Class<T>): T =
    ViewModelProvider(this as ComponentActivity)[type]

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
