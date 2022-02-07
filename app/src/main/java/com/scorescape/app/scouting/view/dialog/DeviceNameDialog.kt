package com.scorescape.app.scouting.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.components.BasicInputField
import com.scorescape.app.scouting.components.DialogScaffold
import com.scorescape.app.scouting.components.SmallButton
import com.scorescape.app.scouting.viewmodel.HomePageViewModel

@Composable
fun DeviceNameDialog(viewModel: HomePageViewModel) {
    val context = LocalContext.current
    if (viewModel.showingDeviceEditDialog) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_user_avatar),
            contentDescription = stringResource(id = R.string.ic_user_avatar_content_desc),
            title = stringResource(id = R.string.home_page_device_edit_dialog_title),
            onDismissRequest = {
                viewModel.showingDeviceEditDialog = false
            }
        ) {
            Column {
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_user_blank),
                    contentDescription = stringResource(id = R.string.ic_user_blank_content_desc),
                    hint = stringResource(id = R.string.home_page_device_edit_dialog_hint),
                    textFieldValue = viewModel.deviceEditNameText,
                    onValueChange = { value ->
                        viewModel.deviceEditNameText = value
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
                            showingDeviceEditDialog = false
                            applyDeviceNameChange(context)
                        }
                    },
                    color = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 25.dp)
                )
            }
        }
    }
}