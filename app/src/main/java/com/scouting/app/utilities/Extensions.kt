package com.scouting.app.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

@Composable
fun Modifier.longPressEffect(offset: Float?) : Modifier = this.composed {
    Modifier
        .zIndex(offset?.let { 1f } ?: 0f)
        .graphicsLayer {
            with(offset ?: 0f) { translationY = this }
            offset?.let {
                scaleX = 1.02F
                scaleY = 1.02F
            }
        }
}