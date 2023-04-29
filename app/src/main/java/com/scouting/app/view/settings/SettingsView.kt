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
import com.scouting.app.misc.AllianceType
import com.scouting.app.misc.RequestCode
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.ScoutingType
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
                                text = "${viewModel.deviceAlliancePosition.name} ${viewModel.deviceRobotPosition}",
                                onClick = { viewModel.showingDevicePositionDialog = true },
                                color = if (viewModel.deviceAlliancePosition == AllianceType.RED) {
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
                                    text = viewModel.competitionScheduleFileName,
                                    onClick = {
                                        context.requestFilePicker(
                                            code = RequestCode.COMPETITION_SCHEDULE_FILE_PICK,
                                            type = arrayOf("csv")
                                        )
                                    },
                                    color = NeutralGrayLight,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                AnimatedVisibility(visible = viewModel.competitionScheduleFileName != "NONE") {
                                    IconButton(
                                        onClick = {
                                            scoutingScheduleManager.resetManagerMatch()
                                            preferences.apply {
                                                encode("COMPETITION_MODE", false)
                                                encode("COMPETITION_SCHEDULE_FILE_NAME", "NONE")
                                            }
                                            viewModel.apply {
                                                competitionMode = false
                                                competitionScheduleFileName = "NONE"
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MediumButton(
                                    text = viewModel.pitScheduleFileName,
                                    onClick = {
                                        context.requestFilePicker(
                                            code = RequestCode.PIT_SCOUTING_SCHEDULE_FILE_PICK,
                                            type = arrayOf("csv")
                                        )
                                    },
                                    color = NeutralGrayLight,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                AnimatedVisibility(visible = viewModel.pitScheduleFileName != "NONE") {
                                    IconButton(
                                        onClick = {
                                            preferences.apply {
                                                encode("PIT_SCOUTING_MODE", false)
                                                encode("PIT_SCHEDULE_FILE_NAME", "NONE")
                                            }
                                            viewModel.apply {
                                                pitScoutingMode = false
                                                pitScheduleFileName = "NONE"
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
                    AnimatedVisibility(visible = viewModel.competitionScheduleFileName != "NONE") {
                        SettingsPreference(
                            title = stringResource(id = R.string.settings_competition_mode_toggle_title),
                            endContent = {
                                Switch(
                                    checked = viewModel.competitionMode,
                                    onCheckedChange = {
                                        viewModel.apply {
                                            scheduledScoutingModeType = ScoutingType.MATCH
                                            showingScheduledScoutingModeDialog = true
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
                                viewModel.apply {
                                    scheduledScoutingModeType = ScoutingType.MATCH
                                    showingScheduledScoutingModeDialog = true
                                }
                            }
                        )
                    }
                    AnimatedVisibility(visible = viewModel.pitScheduleFileName != "NONE") {
                        SettingsPreference(
                            title = stringResource(id = R.string.settings_pit_scouting_mode_title),
                            endContent = {
                                Switch(
                                    checked = viewModel.pitScoutingMode,
                                    onCheckedChange = {
                                        viewModel.apply {
                                            scheduledScoutingModeType = ScoutingType.PIT
                                            showingScheduledScoutingModeDialog = true
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
                                viewModel.apply {
                                    scheduledScoutingModeType = ScoutingType.PIT
                                    showingScheduledScoutingModeDialog = true
                                }
                            }
                        )
                    }
                    SettingsDivider(modifier = Modifier.padding(vertical = 50.dp))
                    SettingsPreference(
                        title = stringResource(id = R.string.settings_choose_default_template_title),
                        endContent = {
                            MediumButton(
                                text = viewModel.defaultMatchTemplateFileName,
                                onClick = {
                                    context.requestFilePicker(
                                        code = RequestCode.MATCH_TEMPLATE_FILE_PICK,
                                        type = arrayOf("json")
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
                                text = viewModel.defaultMatchOutputFileName.text,
                                onClick = {
                                    viewModel.apply {
                                        fileNameEditingType = ScoutingType.MATCH
                                        showingFileNameDialog = true
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
                                text = viewModel.defaultPitTemplateFileName,
                                onClick = {
                                    context.requestFilePicker(
                                        code = RequestCode.PIT_TEMPLATE_FILE_PICK,
                                        type = arrayOf("json")
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
                                text = viewModel.defaultPitOutputFileName.text,
                                onClick = {
                                    viewModel.apply {
                                        fileNameEditingType = ScoutingType.PIT
                                        showingFileNameDialog = true
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