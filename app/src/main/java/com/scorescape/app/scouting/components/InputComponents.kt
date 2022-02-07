package com.scorescape.app.scouting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.theme.NeutralGrayMedium

@Composable
fun BasicInputField(
    icon: Painter? = null,
    contentDescription: String? = null,
    hint: String,
    enabled: Boolean = true,
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        enabled = enabled,
        modifier = modifier,
        value = textFieldValue,
        placeholder = @Composable {
            Text(
                text = hint,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = NeutralGrayMedium,
            cursorColor = MaterialTheme.colors.onBackground,
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        leadingIcon = @Composable {
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    )
}

@Composable
fun CounterBar(
    onValueChange: (Int) -> Unit,
    incrementStep: Int = 1,
    modifier: Modifier = Modifier
) {
    var temporaryCount by remember { mutableStateOf(0) }
    Box {
        Row(
            modifier = modifier
                .background(
                    color = NeutralGrayMedium,
                    shape = RoundedCornerShape(50.dp)
                )
                .height(55.dp)
                .widthIn(min = 220.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    temporaryCount += incrementStep
                    onValueChange.invoke(temporaryCount)
                },
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_circle),
                    contentDescription = stringResource(id = R.string.ic_add_circle_content_desc),
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = {
                    temporaryCount -= incrementStep
                    onValueChange.invoke(temporaryCount)
                },
                modifier = Modifier.padding(end = 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_subtract_circle),
                    contentDescription = stringResource(id = R.string.ic_subtract_circle_content_desc),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Text(
            text = temporaryCount.toString(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LabeledCounter(
    text: String,
    incrementStep: Int = 1,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    counterModifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2
        )
        CounterBar(
            onValueChange = onValueChange,
            incrementStep = incrementStep,
            modifier = counterModifier
        )
    }
}

@Composable
fun RatingBar(
    values: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentlySelected by remember { mutableStateOf(0) }
    Row(modifier = modifier) {
        repeat(values) { index ->
            Card(
                shape = MaterialTheme.shapes.medium,
                backgroundColor = if (index == currentlySelected)
                    MaterialTheme.colors.primary else NeutralGrayMedium,
                elevation = 0.dp,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 10.dp)
                    .clickable {
                        currentlySelected = index
                        onValueChange.invoke(index + 1)
                    }
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun LabeledRatingBar(
    text: String,
    values: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2
        )
        RatingBar(values = values, onValueChange = onValueChange)
    }
}