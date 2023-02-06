package com.scouting.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.theme.ErrorRed
import com.scouting.app.theme.NeutralGrayMedium
import com.scouting.app.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInputField(
    icon: Painter? = null,
    contentDescription: String? = null,
    hint: String,
    enabled: Boolean = true,
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = TextAlign.Start
) {
    TextField(
        enabled = enabled,
        modifier = modifier,
        value = textFieldValue,
        textStyle = MaterialTheme.typography.headlineSmall,
        placeholder = @Composable {
            Text(
                text = hint,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = textAlign
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = NeutralGrayMedium,
            cursorColor = MaterialTheme.colorScheme.onBackground,
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
                    tint = MaterialTheme.colorScheme.onBackground,
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true
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
                modifier = Modifier.padding(start = 5.dp),
                enabled = enabled,
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onBackground
                )
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
                modifier = Modifier.padding(end = 5.dp),
                enabled = enabled,
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onBackground
                )
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
            style = MaterialTheme.typography.bodyLarge,
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
    counterModifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
        CounterBar(
            onValueChange = onValueChange,
            incrementStep = incrementStep,
            modifier = counterModifier,
            enabled = enabled
        )
    }
}

@Composable
fun LabeledTriCounter(
    text1: String,
    text2: String,
    text3: String,
    onValueChange1: (Int) -> Unit,
    onValueChange2: (Int) -> Unit,
    onValueChange3: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val onValueChangeItems = listOf(onValueChange1, onValueChange2, onValueChange3)
        val textItems = listOf(text1, text2, text3)
        onValueChangeItems.forEachIndexed { index, item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .weight(1F)
                    .padding(vertical = 20.dp, horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = textItems[index],
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                CounterBar(onValueChange = item, enabled = enabled)
            }
        }
    }
}

@Composable
fun RatingBar(
    values: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    customTextValues: List<String>? = null,
    allianceSelectionColor: Boolean? = false,
    customColor: Color? = null,
    enabled: Boolean = true,
    startingSelectedIndex: Int = 0
) {
    var currentlySelected by remember { mutableStateOf(startingSelectedIndex) }
    Row(modifier = modifier) {
        repeat(values) { index ->
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (index == currentlySelected) {
                        if (allianceSelectionColor == true) {
                            when (index) {
                                0 -> ErrorRed
                                1 -> PrimaryBlue
                                else -> PrimaryBlue
                            }
                        } else {
                            customColor ?: MaterialTheme.colorScheme.primary
                        }
                    } else {
                        NeutralGrayMedium
                    }
                ),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier
                    //.size(50.dp)
                    .padding(start = 10.dp)
                    .composed {
                        if (enabled) {
                            clickable {
                                currentlySelected = index
                                onValueChange.invoke(index + 1)
                            }
                        } else Modifier
                    }
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = if (customTextValues == null) {
                            (index + 1).toString()
                        } else {
                            customTextValues[index]
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(15.dp)
                            .align(Alignment.CenterVertically)
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
        RatingBar(values = values, onValueChange = onValueChange, enabled = enabled)
    }
}

@Composable
fun SettingsPreference(
    title: String,
    subtitle: String? = null,
    icon: Painter? = null,
    contentDescription: String? = null,
    onClickAction: (() -> Unit) = {},
    endContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickAction.invoke() }
            .padding(horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.6F)
        ) {
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = contentDescription
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        endContent?.invoke()
    }
}