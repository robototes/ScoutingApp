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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.scouting.app.components.LabeledTriCounter
import com.scouting.app.model.TemplateItem
import com.scouting.app.model.TemplateTypes
import com.scouting.app.viewmodel.InMatchViewModel

/**@Composable
fun TemplateLoadView(
    viewModel: InMatchViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(top = 20.dp)
    ) {
        itemsIndexed(getListResource()) { _, item ->
            when (item.type) {
                TemplateTypes.SCORE_BAR -> {
                    var tempItemState by remember { mutableStateOf(0) }
                    LabeledCounter(
                        text = item.text,
                        onValueChange = {
                            tempItemState = it
                            item.itemValueInt = it
                        },
                        incrementStep = 1,
                        modifier = Modifier.padding(30.dp)
                    )
                }
                TemplateTypes.CHECK_BOX -> {
                    var tempItemState by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.padding(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempItemState,
                            onCheckedChange = {
                                tempItemState = it
                                item.itemValueBoolean = it
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
                    var tempItemState by remember { mutableStateOf(TextFieldValue()) }
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_text_format_center),
                        contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                        hint = item.text,
                        textFieldValue = tempItemState,
                        onValueChange = {
                            tempItemState = it
                            item.itemValueString = it.text
                        },
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                    )
                }
                TemplateTypes.RATING_BAR -> {
                    LabeledRatingBar(
                        text = item.text,
                        values = 5,
                        onValueChange = { item.itemValueInt = it },
                        modifier = Modifier.padding(30.dp)
                    )
                }
                TemplateTypes.TRI_SCORING -> {
                    LabeledTriCounter(
                        text1 = item.text,
                        text2 = item.text2.toString(),
                        text3 = item.text3.toString(),
                        onValueChange1 = { item.itemValueInt = it },
                        onValueChange2 = { item.itemValue2Int = it },
                        onValueChange3 = { item.itemValue3Int = it }
                    )
                }
            }
        }
    }
}**/