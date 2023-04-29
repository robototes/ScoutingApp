package com.scouting.app.view.template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.components.*
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateItem
import com.scouting.app.viewmodel.TemplateEditorViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TemplateListSheet(
    viewModel: TemplateEditorViewModel,
    sheetState: ModalBottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SheetHandle()
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.currentListResource.add(
                    TemplateItem(
                        id = UUID.randomUUID().toString(),
                        text = "",
                        type = TemplateTypes.SCORE_BAR,
                        saveKey = ""
                    )
                )
                coroutineScope.launch { sheetState.hide() }
            }
        ) {
            LabeledCounter(
                text = stringResource(id = R.string.template_editor_score_bar_placeholder),
                onValueChange = {},
                incrementStep = 5,
                modifier = Modifier.padding(30.dp),
                enabled = false
            )
        }
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.currentListResource.add(
                    TemplateItem(
                        id = UUID.randomUUID().toString(),
                        text = "",
                        type = TemplateTypes.RATING_BAR,
                        saveKey = ""
                    )
                )
                coroutineScope.launch { sheetState.hide() }
            }
        ) {
            LabeledRatingBar(
                text = stringResource(id = R.string.template_editor_rating_bar_placeholder),
                values = 5,
                onValueChange = {},
                modifier = Modifier.padding(30.dp),
                enabled = false
            )
        }
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.currentListResource.add(
                    TemplateItem(
                        id = UUID.randomUUID().toString(),
                        text = "",
                        type = TemplateTypes.TEXT_FIELD,
                        saveKey = ""
                    )
                )
                coroutineScope.launch { sheetState.hide() }
            }
        ) {
            SpacedRow(modifier = Modifier.padding(vertical = 30.dp)) {
                Text(
                    text = stringResource(id = R.string.template_editor_text_field_placeholder),
                    style = MaterialTheme.typography.headlineSmall
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
                viewModel.currentListResource.add(
                    TemplateItem(
                        id = UUID.randomUUID().toString(),
                        text = "",
                        type = TemplateTypes.PLAIN_TEXT,
                        saveKey = ""
                    )
                )
                coroutineScope.launch { sheetState.hide() }
            }
        ) {
            SpacedRow(modifier = Modifier.padding(vertical = 30.dp)) {
                Text(
                    text = stringResource(id = R.string.template_editor_note_text_placeholder),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(id = R.string.template_editor_note_text_hint),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.currentListResource.add(
                    TemplateItem(
                        id = UUID
                            .randomUUID()
                            .toString(),
                        text = "",
                        type = TemplateTypes.CHECK_BOX,
                        saveKey = ""
                    )
                )
                coroutineScope.launch { sheetState.hide() }
            }
        ) {
            SpacedRow(modifier = Modifier.padding(vertical = 30.dp)) {
                Text(
                    text = stringResource(id = R.string.template_editor_checkbox_placeholder),
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                        modifier = Modifier.size(40.dp),
                        enabled = false
                    )
                    Text(
                        text = stringResource(id = R.string.template_editor_checkbox_hint),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
        BorderedCard(
            modifier = Modifier.clickable {
                viewModel.currentListResource.add(
                    TemplateItem(
                        id = UUID
                            .randomUUID()
                            .toString(),
                        text = "",
                        text2 = "",
                        text3 = "",
                        type = TemplateTypes.TRI_SCORING,
                        saveKey = "",
                        saveKey2 = "",
                        saveKey3 = ""
                    )
                )
                coroutineScope.launch { sheetState.hide() }
            }
        ) {
            LabeledTriCounter(
                text1 = stringResource(id = R.string.template_editor_label_1_preview),
                text2 = stringResource(id = R.string.template_editor_label_2_preview),
                text3 = stringResource(id = R.string.template_editor_label_3_preview),
                onValueChange1 = {},
                onValueChange2 = {},
                onValueChange3 = {},
                enabled = false
            )
        }
        BorderedCard(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable {
                    viewModel.currentListResource.add(
                        TemplateItem(
                            id = UUID
                                .randomUUID()
                                .toString(),
                            text = "",
                            text2 = "",
                            text3 = "",
                            text4 = "",
                            type = TemplateTypes.TRI_BUTTON,
                            saveKey = ""
                        )
                    )
                    coroutineScope.launch { sheetState.hide() }
                }
        ) {
            val buttonLabel = stringResource(id = R.string.template_editor_tri_button_format_text)
            TriButtonBlock(
                headerText = stringResource(id = R.string.template_editor_tri_button_label),
                buttonLabelOne = "$buttonLabel 1",
                buttonLabelTwo = "$buttonLabel 2",
                buttonLabelThree = "$buttonLabel 3",
                onValueChange = {},
                enabled = false,
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)
            )
        }
        BorderedCard(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable {
                    viewModel.currentListResource.add(
                        TemplateItem(
                            id = UUID
                                .randomUUID()
                                .toString(),
                            text = "",
                            type = TemplateTypes.IMAGE,
                            saveKey = ""
                        )
                    )
                    coroutineScope.launch { sheetState.hide() }
                }
        ) {
            // probably want to polish this further in the future
            Row(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.template_edit_image_item_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}