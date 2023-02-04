package com.scouting.app.view.dialog

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.SmallButton
import com.scouting.app.viewmodel.SettingsViewModel

@Composable
fun FileNameDialog(viewModel: SettingsViewModel, navController: NavController) {
    val context = navController.context as MainActivity
    if (viewModel.showingFileNameDialog.value) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_save_icon),
            contentDescription = stringResource(id = R.string.ic_save_icon_content_desc),
            title = stringResource(id = R.string.settings_choose_default_output_location_dialog_title),
            subtitle = stringResource(id = R.string.settings_choose_default_output_location_dialog_subtitle),
            onDismissRequest = {
                viewModel.showingFileNameDialog.value = false
            }
        ) {
            Column {
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_save_file),
                    contentDescription = stringResource(id = R.string.ic_save_file_content_desc),
                    hint = stringResource(id = R.string.home_page_device_edit_dialog_hint),
                    textFieldValue = viewModel.defaultOutputFileName.value,
                    onValueChange = { value ->
                        viewModel.defaultOutputFileName.value = value
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
                            showingFileNameDialog.value = false
                            applyOutputFileNameChange(context)
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