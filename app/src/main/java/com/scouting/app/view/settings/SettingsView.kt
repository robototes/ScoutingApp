package com.scouting.app.view

import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.scouting.app.misc.MatchManager
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.view.dialog.FileNameDialog
import com.scouting.app.view.settings.DevicePositionDialog
import com.scouting.app.viewmodel.SettingsViewModel
import java.io.File

@Composable
fun SettingsView(navController: NavController, matchManager: MatchManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(SettingsViewModel::class.java)
    LaunchedEffect(true) {
        val preferences = context.getPreferences(Context.MODE_PRIVATE)
        viewModel.apply {
            deviceRobotPosition.value = preferences.getInt("DEVICE_ROBOT_POSITION", 1)
            deviceAlliancePosition.value =
                preferences.getString("DEVICE_ALLIANCE_POSITION", "RED")!!
            defaultTemplateFileName.value = File(
                preferences.getString("DEFAULT_TEMPLATE_FILE_NAME", "file.json")!!
            ).name
            competitionScheduleFileName.value =
                preferences.getString("COMPETITION_SCHEDULE_FILE_NAME", "NONE")!!
            competitionMode.value = preferences.getBoolean("COMPETITION_MODE", false)
            this.matchManager = matchManager
        }
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
                                        context = context,
                                        code = 2412,
                                        type = "json"
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
                                          context = context,
                                          code = 2413,
                                          type = "csv"
                                      )
                                },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_competition_mode_toggle_title),
                        endContent = {
                            Switch(
                                checked = viewModel.competitionMode.value,
                                onCheckedChange = {
                                    viewModel.apply {
                                        competitionMode.value = it
                                        if (it) {
                                            beginCompetitionMode(context)
                                        } else {
                                            endCompetitionMode(context)
                                            matchManager.resetManager(context)
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(0.2F)
                                )
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp),
                        onClickAction = {
                            viewModel.apply {
                                competitionMode.let {
                                    it.value = !it.value
                                }
                                if (competitionMode.value) {
                                    beginCompetitionMode(context)
                                } else {
                                    endCompetitionMode(context)
                                    matchManager.resetManager(context)
                                }
                            }
                        }
                    )
                }
            }
            FileNameDialog(viewModel, navController)
            DevicePositionDialog(viewModel, navController)
        }
    }
}