package com.scouting.app.view.settings

import android.content.Context.MODE_PRIVATE
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.scouting.app.viewmodel.SettingsViewModel

@Composable
fun SettingsView(navController: NavController, matchManager: MatchManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(SettingsViewModel::class.java)
    val preferences = context.getPreferences(MODE_PRIVATE)
    LaunchedEffect(true) {
        viewModel.apply {
            loadSavedPreferences(context)
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
                        title = stringResource(id = R.string.settings_tablet_configuration_title),
                        endContent = {
                            MediumButton(
                                text = "${viewModel.deviceAlliancePosition.value} ${viewModel.deviceRobotPosition.value}",
                                onClick = { viewModel.showingDevicePositionDialog.value = true },
                                color = if (viewModel.deviceAlliancePosition.value == "RED") {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        },
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_load_competition_schedule_title),
                        endContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MediumButton(
                                    text = viewModel.competitionScheduleFileName.value,
                                    onClick = {
                                        viewModel.requestFilePicker(
                                            context = context,
                                            code = 2413,
                                            type = "csv"
                                        )
                                    },
                                    color = NeutralGrayLight,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                AnimatedVisibility(visible = viewModel.competitionMode.value) {
                                    IconButton(
                                        onClick = {
                                            matchManager.resetManager(context)
                                            preferences.edit()
                                                .putBoolean("COMPETITION_MODE", false)
                                                .putString("COMPETITION_SCHEDULE_FILE_NAME", "NONE")
                                                .apply()
                                            viewModel.competitionMode.value = false
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_trash_can),
                                            contentDescription = stringResource(id = R.string.ic_trash_can_content_desc)
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_competition_mode_toggle_title),
                        endContent = {
                            Switch(
                                checked = viewModel.competitionMode.value,
                                onCheckedChange = {
                                    viewModel.showingCompetitionModeDialog.value = true
                                },
                                colors = SwitchDefaults.colors(
                                    uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(0.2F)
                                )
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp),
                        onClickAction = {
                            viewModel.showingCompetitionModeDialog.value = true
                        }
                    )
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
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_output_location_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultMatchOutputFileName.value.text,
                                onClick = { viewModel.showingFileNameDialog.value = true },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_template_pit_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultPitTemplateFileName.value,
                                onClick = {
                                    viewModel.requestFilePicker(
                                        context = context,
                                        code = 2414,
                                        type = "json"
                                    )
                                },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_pit_output_location_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultPitTemplateOutputFileName.value,
                                onClick = {
                                    viewModel.showingFileNameDialog.value = true
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
            CompetitionModeDialog(viewModel, navController)
        }
    }
}