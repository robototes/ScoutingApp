package com.scorescape.app.scouting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scorescape.app.scouting.theme.NeutralGrayMedium

@Composable
fun SheetHandle() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp, bottom = 15.dp)
                .width(100.dp)
                .height(4.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = NeutralGrayMedium,
            elevation = 0.dp
        ) { }
    }
}

@Composable
fun BorderedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        border = BorderStroke(2.dp, MaterialTheme.colors.onBackground),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        content.invoke()
    }
}

@Composable
fun DottedRoundBox(
    modifier: Modifier = Modifier,
    height: Dp,
    content: @Composable () -> Unit
) {
    MaterialTheme.colors.let { themeColors ->
        Box(modifier = modifier) {
            Canvas(Modifier.fillMaxWidth().height(height)) {
                drawRoundRect(
                    color = themeColors.onBackground,
                    style = Stroke(
                        width = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = CornerRadius(15.0F, 15.0F)
                )
            }
            content.invoke()
        }
    }
}