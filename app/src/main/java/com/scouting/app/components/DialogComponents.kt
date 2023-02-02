package com.scouting.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DialogScaffold(
    icon: Painter,
    contentDescription: String,
    title: String,
    subtitle: String? = null,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .padding(top = 25.dp, start = 30.dp)
                        .size(40.dp),
                    tint = MaterialTheme.colors.onBackground
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(start = 20.dp, top = 25.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
            content.invoke()
        }
    }
}