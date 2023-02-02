package com.scouting.app.view.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.components.SmallButton
import com.scouting.app.viewmodel.TemplateEditorViewModel
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.DialogScaffold
import com.scouting.app.model.TemplateTypes

@Composable
fun EditTemplateDialog(viewModel: TemplateEditorViewModel) {
    if (viewModel.showingEditDialog) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_edit_pen),
            contentDescription = stringResource(id = R.string.ic_edit_pen_content_desc),
            title = stringResource(id = R.string.template_editor_edit_dialog_header),
            onDismissRequest = {
                viewModel.showingEditDialog = false
            }
        ) {
            // CRASHES IF ALL ITEMS IN THE LIST ARE REMOVED (SIZE = 0, INDEX = 0)
            val currentEditItem = viewModel.let { it.currentListResource[it.currentEditItemIndex] }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var textFieldValueLabel by remember {
                    mutableStateOf(TextFieldValue(currentEditItem.text))
                }
                var textFieldValueLabel2 by remember {
                    mutableStateOf(TextFieldValue(currentEditItem.text2.toString()))
                }
                var textFieldValueLabel3 by remember {
                    mutableStateOf(TextFieldValue(currentEditItem.text3.toString()))
                }
                var textFieldValueSaveKey by remember {
                    mutableStateOf(TextFieldValue(currentEditItem.saveKey))
                }
                var textFieldValueSaveKey2 by remember {
                    mutableStateOf(TextFieldValue(currentEditItem.saveKey2.toString()))
                }
                var textFieldValueSaveKey3 by remember {
                    mutableStateOf(TextFieldValue(currentEditItem.saveKey3.toString()))
                }
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_text_format_center),
                    contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                    hint = stringResource(id = R.string.template_editor_edit_dialog_field_hint),
                    textFieldValue = textFieldValueLabel,
                    onValueChange = {
                        textFieldValueLabel = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                )
                if (currentEditItem.type == TemplateTypes.TRI_SCORING) {
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_text_format_center),
                        contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                        hint = stringResource(id = R.string.template_editor_edit_dialog_field_2_hint),
                        textFieldValue = textFieldValueLabel2,
                        onValueChange = {
                            textFieldValueLabel2 = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_text_format_center),
                        contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                        hint = stringResource(id = R.string.template_editor_edit_dialog_field_3_hint),
                        textFieldValue = textFieldValueLabel3,
                        onValueChange = {
                            textFieldValueLabel3 = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                }
                // Plain text has no user input, thus nothing to save
                if (currentEditItem.type != TemplateTypes.PLAIN_TEXT) {
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_save_file),
                        contentDescription = stringResource(id = R.string.ic_save_file_content_desc),
                        hint = stringResource(id = R.string.template_editor_edit_dialog_save_key_hint),
                        textFieldValue = textFieldValueSaveKey,
                        onValueChange = {
                            textFieldValueSaveKey = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                }
                if (currentEditItem.type == TemplateTypes.TRI_SCORING) {
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_save_file),
                        contentDescription = stringResource(id = R.string.ic_save_file_content_desc),
                        hint = stringResource(id = R.string.template_editor_edit_dialog_save_key_2_hint),
                        textFieldValue = textFieldValueSaveKey2,
                        onValueChange = {
                            textFieldValueSaveKey2 = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_save_file),
                        contentDescription = stringResource(id = R.string.ic_save_file_content_desc),
                        hint = stringResource(id = R.string.template_editor_edit_dialog_save_key_3_hint),
                        textFieldValue = textFieldValueSaveKey3,
                        onValueChange = {
                            textFieldValueSaveKey3 = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallButton(
                        text = stringResource(id = R.string.home_page_device_edit_dialog_save_button),
                        icon = painterResource(id = R.drawable.ic_checkmark_outline),
                        contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                        onClick = {
                            viewModel.apply {
                                currentListResource[currentEditItemIndex].apply {
                                    text = textFieldValueLabel.text
                                    text2 = textFieldValueLabel2.text
                                    text3 = textFieldValueLabel3.text
                                    saveKey = textFieldValueSaveKey.text
                                    saveKey2 = textFieldValueSaveKey2.text
                                    saveKey3 = textFieldValueSaveKey3.text
                                }
                                showingEditDialog = false
                            }
                        },
                        color = MaterialTheme.colors.primaryVariant
                    )
                    SmallButton(
                        text = stringResource(id = R.string.template_editor_edit_dialog_discard_button),
                        icon = painterResource(id = R.drawable.ic_trash_can),
                        contentDescription = stringResource(id = R.string.ic_trash_can_content_desc),
                        onClick = {
                            viewModel.apply {
                                currentListResource.apply {
                                    remove(this[currentEditItemIndex])
                                }
                                showingEditDialog = false
                            }
                        },
                        color = MaterialTheme.colors.secondaryVariant
                    )
                }
            }
        }
    }
}