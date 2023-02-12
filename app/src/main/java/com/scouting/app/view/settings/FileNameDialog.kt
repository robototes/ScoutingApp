package com.scouting.app.view.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.SmallButton
import com.scouting.app.misc.ScoutingType
import com.scouting.app.viewmodel.SettingsViewModel

@Composable
fun FileNameDialog(viewModel: SettingsViewModel) {
    val currentEditingTextFieldValue = viewModel.let {
        if (it.fileNameEditingType == ScoutingType.PIT) {
            it.defaultPitOutputFileName
        } else {
            it.defaultMatchOutputFileName
        }
    }
    if (viewModel.showingFileNameDialog) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_data_unstructured),
            contentDescription = stringResource(id = R.string.ic_data_unstructured_content_desc),
            title = stringResource(id = R.string.settings_choose_default_output_location_dialog_title),
            onDismissRequest = {
                viewModel.showingFileNameDialog = false
            }
        ) {
            Column {
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_edit_pen),
                    contentDescription = stringResource(id = R.string.ic_edit_pen_content_desc),
                    hint = stringResource(id = R.string.settings_choose_default_output_location_dialog_input_hint),
                    textFieldValue = currentEditingTextFieldValue,
                    onValueChange = { value ->
                        viewModel.apply {
                            if (fileNameEditingType == ScoutingType.PIT) {
                                defaultPitOutputFileName = value
                            } else {
                                defaultMatchOutputFileName = value
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp)
                )
                SmallButton(
                    text = stringResource(id = R.string.home_page_device_edit_dialog_save_button),
                    icon = painterResource(id = R.drawable.ic_checkmark_outline),
                    contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                    onClick = {
                        viewModel.apply {
                            showingFileNameDialog = false
                            applyOutputFileNameChange(currentEditingTextFieldValue.text)
                            val finalValue = TextFieldValue(
                                viewModel.processDefaultOutputFileName(
                                    currentEditingTextFieldValue.text
                                )
                            )
                            if (viewModel.fileNameEditingType == ScoutingType.PIT) {
                                defaultPitOutputFileName = finalValue
                            } else {
                                defaultMatchOutputFileName = finalValue
                            }
                        }
                    },
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 25.dp)
                )
            }
        }
    }
}