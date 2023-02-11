package com.scouting.app.view.settings

import androidx.compose.foundation.layout.Column
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
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.RatingBar
import com.scouting.app.components.SmallButton
import com.scouting.app.components.SpacedRow
import com.scouting.app.viewmodel.SettingsViewModel

@Composable
fun DevicePositionDialog(viewModel: SettingsViewModel, navController: NavController) {
    val context = navController.context as MainActivity
    if (viewModel.showingDevicePositionDialog.value) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_machine_learning),
            contentDescription = stringResource(id = R.string.ic_machine_learning_content_desc),
            title = stringResource(id = R.string.settings_tablet_configuration_title),
            onDismissRequest = {
                viewModel.showingDevicePositionDialog.value = false
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SpacedRow(
                    modifier = Modifier.padding(end = 10.dp, top = 40.dp, bottom = 40.dp),
                    horizontalPadding = 20.dp
                ) {
                    val alliancePositionValues =
                        context.resources.getStringArray(R.array.alliance_position_values)
                    RatingBar(
                        values = 2,
                        customTextValues = alliancePositionValues.asList(),
                        onValueChange = {
                            viewModel.deviceAlliancePosition.value = alliancePositionValues[it - 1]
                        },
                        allianceSelectionColor = true,
                        startingSelectedIndex = alliancePositionValues.indexOf(
                            viewModel.deviceAlliancePosition.value
                        ) + 1
                    )
                    RatingBar(
                        values = 3,
                        onValueChange = {
                            viewModel.deviceRobotPosition.value = it
                        },
                        customColor = when (viewModel.deviceAlliancePosition.value) {
                            "RED" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        startingSelectedIndex = viewModel.deviceRobotPosition.value
                    )
                }
                SmallButton(
                    text = stringResource(id = R.string.home_page_device_edit_dialog_save_button),
                    icon = painterResource(id = R.drawable.ic_checkmark_outline),
                    contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                    onClick = {
                        viewModel.apply {
                            showingDevicePositionDialog.value = false
                            applyDevicePositionChange()
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