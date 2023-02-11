package com.scouting.app.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.scouting.app.components.SettingsDivider
import com.scouting.app.components.SettingsPreference
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV

@Composable
fun SettingsView(navController: NavController, scoutingScheduleManager: ScoutingScheduleManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(SettingsViewModel::class.java)
    val preferences = MMKV.defaultMMKV()
    LaunchedEffect(true) {
        viewModel.apply {
            loadSavedPreferences()
            this.scoutingScheduleManager = scoutingScheduleManager
        }
    }
    ScoutingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                LargeHeaderBar(
                    title = stringResource(id = R.string.settings_header_title),
                    navController = navController
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    SettingsDivider(modifier = Modifier.padding(top = 50.dp))
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
                                AnimatedVisibility(visible = viewModel.competitionScheduleFileName.value != "NONE") {
                                    IconButton(
                                        onClick = {
                                            scoutingScheduleManager.resetManagerMatch()
                                            preferences.apply {
                                                encode("COMPETITION_MODE", false)
                                                encode("COMPETITION_SCHEDULE_FILE_NAME", "NONE")
                                            }
                                            viewModel.apply {
                                                competitionMode.value = false
                                                competitionScheduleFileName.value = "NONE"
                                            }
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
                        title = stringResource(id = R.string.settings_load_pit_schedule_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.pitScheduleFileName.value,
                                onClick = {
                                    viewModel.requestFilePicker(
                                        context = context,
                                        code = 2415,
                                        type = "csv"
                                    )
                                },
                                color = NeutralGrayLight,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            AnimatedVisibility(visible = viewModel.pitScheduleFileName.value != "NONE") {
                                IconButton(
                                    onClick = {
                                        preferences.apply {
                                            encode("PIT_SCOUTING_MODE", false)
                                            encode("PIT_SCHEDULE_FILE_NAME", "NONE")
                                        }
                                        viewModel.apply {
                                            pitScoutingMode.value = false
                                            pitScheduleFileName.value = "NONE"
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_trash_can),
                                        contentDescription = stringResource(id = R.string.ic_trash_can_content_desc)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    AnimatedVisibility(visible = viewModel.pitScheduleFileName.value != "NONE") {
                        SettingsPreference(
                            title = stringResource(id = R.string.settings_pit_scouting_mode_title),
                            endContent = {
                                Switch(
                                    checked = viewModel.pitScoutingMode.value,
                                    onCheckedChange = {
                                        viewModel.apply {
                                            scheduledScoutingModeType.value = false
                                            showingScheduledScoutingModeDialog.value = true
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(
                                            0.2F
                                        )
                                    )
                                )
                            },
                            modifier = Modifier.padding(top = 50.dp),
                            onClickAction = {
                                viewModel.showingScheduledScoutingModeDialog.value = true
                            }
                        )
                    }
                    AnimatedVisibility(visible = viewModel.competitionScheduleFileName.value != "NONE") {
                        SettingsPreference(
                            title = stringResource(id = R.string.settings_competition_mode_toggle_title),
                            endContent = {
                                Switch(
                                    checked = viewModel.competitionMode.value,
                                    onCheckedChange = {
                                        viewModel.showingScheduledScoutingModeDialog.value = true
                                    },
                                    colors = SwitchDefaults.colors(
                                        uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(
                                            0.2F
                                        )
                                    )
                                )
                            },
                            modifier = Modifier.padding(top = 50.dp),
                            onClickAction = {
                                viewModel.showingScheduledScoutingModeDialog.value = true
                            }
                        )
                    }
                    SettingsDivider(modifier = Modifier.padding(vertical = 50.dp))
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_template_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultMatchTemplateFileName.value,
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
                        //modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_output_location_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultMatchOutputFileName.value.text,
                                onClick = {
                                    viewModel.apply {
                                        fileNameEditingType.value = false
                                        showingFileNameDialog.value = true
                                    }
                                },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsDivider(modifier = Modifier.padding(vertical = 50.dp))
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
                        // modifier = Modifier.padding(top = 50.dp)
                    )
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_pit_output_location_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultPitOutputFileName.value.text,
                                onClick = {
                                    viewModel.apply {
                                        fileNameEditingType.value = true
                                        showingFileNameDialog.value = true
                                    }
                                },
                                color = NeutralGrayLight
                            )
                        },
                        modifier = Modifier.padding(top = 50.dp)
                    )
                }
            }
            FileNameDialog(viewModel)
            DevicePositionDialog(viewModel, navController)
            CompetitionModeDialog(viewModel)
        }
    }
}