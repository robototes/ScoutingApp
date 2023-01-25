package com.scouting.app.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.LabeledCounter
import com.scouting.app.components.LabeledRatingBar
import com.scouting.app.model.TemplateItem
import com.scouting.app.model.TemplateTypes

@Composable
fun TemplateLoadView(
    template: List<TemplateItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(top = 20.dp)
    ) {
        itemsIndexed(template) { _, item ->
            when (item.type) {
                TemplateTypes.SCORE_BAR -> {
                    item.itemState = remember { mutableStateOf(0) }
                    LabeledCounter(
                        text = item.text,
                        onValueChange = { (item.itemState as MutableState<Int>).value = it },
                        incrementStep = 1,
                        modifier = Modifier.padding(30.dp)
                    )
                }
                TemplateTypes.CHECK_BOX -> {
                    item.itemState = remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.padding(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = (item.itemState as MutableState<Boolean>).value,
                            onCheckedChange = {
                                (item.itemState as MutableState<Boolean>).value = it
                            }
                        )
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                }
                TemplateTypes.PLAIN_TEXT -> {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(30.dp)
                    )
                }
                TemplateTypes.TEXT_FIELD -> {
                    item.itemState = remember { mutableStateOf(TextFieldValue()) }
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_text_format_center),
                        contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                        hint = item.text,
                        textFieldValue = (item.itemState as MutableState<TextFieldValue>).value,
                        onValueChange = { (item.itemState as MutableState<TextFieldValue>).value = it },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                            .padding(30.dp)
                    )
                }
                TemplateTypes.RATING_BAR -> {
                    item.itemState = remember { mutableStateOf(1) }
                    LabeledRatingBar(
                        text = item.text,
                        values = 5,
                        onValueChange = { (item.itemState as MutableState<Int>).value = it },
                        modifier = Modifier.padding(30.dp)
                    )
                }
            }
        }
    }
}