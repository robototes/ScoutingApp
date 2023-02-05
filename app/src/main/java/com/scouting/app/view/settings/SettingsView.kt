package com.scouting.app.view

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.LargeHeaderBar
import com.scouting.app.components.MediumButton
import com.scouting.app.components.SettingsPreference
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.view.dialog.FileNameDialog
import com.scouting.app.view.settings.DevicePositionDialog
import com.scouting.app.viewmodel.SettingsViewModel
import java.io.File

@Composable
fun SettingsView(navController: NavController) {
    val context = navController.context
    val viewModel = context.getViewModel(SettingsViewModel::class.java)
    LaunchedEffect(true) {
        viewModel.defaultTemplateFileName.value = File(
            (context as MainActivity).getPreferences(Context.MODE_PRIVATE)
            .getString("DEFAULT_TEMPLATE_FILE_NAME", "file.json")!!
        ).name
    }
    ScoutingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                LargeHeaderBar(
                    title = stringResource(id = R.string.settings_header_title),
                    navController = navController
                )
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_template_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultTemplateFileName.value,
                                onClick = {
                                    viewModel.requestFilePicker(
                                        context = context as MainActivity,
                                        code = 2412,
                                        type = "application/json"
                                    )
                                },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_output_location_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultOutputFileName.value.text,
                                onClick = { viewModel.showingFileNameDialog.value = true },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_tablet_configuration_title),
                        endContent = {
                            MediumButton(
                                text = "${viewModel.deviceAlliancePosition.value} ${viewModel.deviceRobotPosition.value + 1}",
                                onClick = { viewModel.showingDevicePositionDialog.value = true },
                                color = if (viewModel.deviceAlliancePosition.value == "RED") {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_load_competition_schedule_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.competitionScheduleFileName.value,
                                onClick = {
                                      viewModel.requestFilePicker(
                                          context = context as MainActivity,
                                          code = 2413,
                                          type = "F"
                                      )
                                },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                }
            }
            FileNameDialog(viewModel, navController)
            DevicePositionDialog(viewModel, navController)
        }
    }
}