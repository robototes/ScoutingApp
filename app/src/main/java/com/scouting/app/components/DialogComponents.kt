package com.scouting.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.background)
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
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 20.dp, top = 25.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 25.dp, top = 10.dp, end = 20.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            content.invoke()
        }
    }
}