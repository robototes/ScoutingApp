package com.scouting.app.view.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.model.TemplateItem
import com.scouting.app.model.TemplateTypes
import com.scouting.app.viewmodel.TemplateEditorViewModel
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.BorderedCard
import com.scouting.app.components.LabeledCounter
import com.scouting.app.components.LabeledRatingBar
import com.scouting.app.components.SheetHandle
import java.util.*

@Composable
fun TemplateListSheet(viewModel: TemplateEditorViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SheetHandle()
        if (viewModel.currentTemplateType == "match") {
            BorderedCard(
                modifier = Modifier.clickable {
                    viewModel.currentListResource.add(
                        TemplateItem("", TemplateTypes.SCORE_BAR, UUID.randomUUID().toString(), null, "")
                    )
                }
            ) {
                LabeledCounter(
                    text = stringResource(id = R.string.template_editor_score_bar_placeholder),
                    onValueChange = {},
                    incrementStep = 5,
                    modifier = Modifier.padding(30.dp)
                )
            }
        } else {
            BorderedCard(
                modifier = Modifier.clickable {
                    viewModel.currentListResource.add(
                        TemplateItem("", TemplateTypes.RATING_BAR, UUID.randomUUID().toString(), null, "")
                    )
                }
            ) {
                LabeledRatingBar(
                    text = stringResource(id = R.string.template_editor_rating_bar_placeholder),
                    values = 5,
                    onValueChange = {},
                    modifier = Modifier.padding(30.dp)
                )
            }
        }
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.currentListResource.add(
                    TemplateItem("", TemplateTypes.TEXT_FIELD, UUID.randomUUID().toString(), null, "")
                )
            }
        ) {
            Row(
                modifier = Modifier.padding(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.template_editor_text_field_placeholder),
                    style = MaterialTheme.typography.body2
                )
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_text_format_center),
                    contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                    hint = stringResource(id = R.string.template_editor_text_field_hint),
                    textFieldValue = TextFieldValue(),
                    onValueChange = {},
                    enabled = false
                )
            }
        }
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.autoListItems.add(
                    TemplateItem("", TemplateTypes.PLAIN_TEXT, UUID.randomUUID().toString(), null, "")
                )
            }
        ) {
            Row(
                modifier = Modifier.padding(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.template_editor_note_text_placeholder),
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = stringResource(id = R.string.template_editor_note_text_hint),
                    style = MaterialTheme.typography.body2
                )
            }
        }
        BorderedCard(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable {
                    viewModel.autoListItems.add(
                        TemplateItem(
                            "",
                            TemplateTypes.CHECK_BOX,
                            UUID
                                .randomUUID()
                                .toString(),
                            null, ""
                        )
                    )
                }
        ) {
            Row(
                modifier = Modifier.padding(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.template_editor_checkbox_placeholder),
                    style = MaterialTheme.typography.body2
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.template_editor_checkbox_hint),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}