package com.scorescape.app.scouting.view.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.components.BasicInputField
import com.scorescape.app.scouting.components.DialogScaffold
import com.scorescape.app.scouting.components.SmallButton
import com.scorescape.app.scouting.viewmodel.TemplateEditorViewModel

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var textFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(viewModel.let {
                            it.currentListResource[it.currentEditItemIndex].text
                        })
                    )
                }
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_text_format_center),
                    contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                    hint = stringResource(id = R.string.template_editor_edit_dialog_field_hint),
                    textFieldValue = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                )
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
                                currentListResource[currentEditItemIndex].text = textFieldValue.text
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