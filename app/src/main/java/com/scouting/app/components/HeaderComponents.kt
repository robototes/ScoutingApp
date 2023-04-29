package com.scouting.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R

@Composable
fun LargeHeaderBar(
    title: String,
    navController: NavController,
    endContent: (@Composable () -> Unit)? = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp, start = 25.dp, end = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc),
                    modifier = Modifier.size(30.dp)
                )
            }
            endContent?.invoke()
        }
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(start = 30.dp)
        )
    }
}

@Composable
fun MediumHeaderBar(
    title: String,
    navController: NavController,
    iconLeft: Painter? = null,
    contentDescription: String? = null,
    onIconLeftClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.padding(start = 25.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc),
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )
        if (iconLeft == null) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 25.dp)
            )
        } else {
            IconButton(
                onClick = onIconLeftClick!!,
                modifier = Modifier.padding(end = 25.dp)
            ) {
                Icon(
                    painter = iconLeft,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}