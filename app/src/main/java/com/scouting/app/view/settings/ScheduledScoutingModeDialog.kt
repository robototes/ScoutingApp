package com.scouting.app.view.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.SmallButton
import com.scouting.app.components.SpacedRow
import com.scouting.app.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV

@Composable
fun CompetitionModeDialog(viewModel: SettingsViewModel) {
    val scoutingType = viewModel.scheduledScoutingModeType.value
    val preferenceKeyPrefix = if (scoutingType) "COMPETITION" else "PIT_SCOUTING"
    val preferenceValue = MMKV.defaultMMKV().decodeBool("${preferenceKeyPrefix}_MODE", false)
    if (viewModel.showingScheduledScoutingModeDialog.value) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_rotate_180),
            contentDescription = stringResource(id = R.string.ic_rotate_180_content_desc),
            title = if (preferenceValue) {
                if (scoutingType) {
                    stringResource(id = R.string.settings_competition_mode_dialog_disable_title)
                } else {
                    stringResource(id = R.string.settings_pit_scouting_mode_dialog_disable_title)
                }
            } else {
                if (scoutingType) {
                    stringResource(id = R.string.settings_competition_mode_dialog_enable_title)
                } else {
                    stringResource(id = R.string.settings_pit_scouting_mode_dialog_enable_title)
                }
            },
            onDismissRequest = {
                viewModel.showingScheduledScoutingModeDialog.value = false
            }
        ) {
            Text(
                text = if (preferenceValue) {
                    if (scoutingType) {
                        stringResource(id = R.string.settings_competition_mode_dialog_disable_subtitle)
                    } else {
                        stringResource(id = R.string.settings_pit_scouting_mode_dialog_disable_subtitle)
                    }
                } else {
                    if (scoutingType) {
                        stringResource(id = R.string.settings_competition_mode_dialog_enable_subtitle)
                    } else {
                        stringResource(id = R.string.settings_pit_scouting_mode_dialog_enable_subtitle)
                    }
                },
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)
            )
            SpacedRow(modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)) {
                SmallButton(
                    text = stringResource(id = R.string.settings_competition_mode_dialog_negative_button_label),
                    icon = painterResource(id = R.drawable.ic_close_outline),
                    contentDescription = stringResource(id = R.string.ic_close_outline_content_desc),
                    onClick = {
                        viewModel.showingScheduledScoutingModeDialog.value = false
                    },
                    color = MaterialTheme.colorScheme.error
                )
                SmallButton(
                    text = stringResource(id = R.string.settings_competition_mode_dialog_positive_button_label),
                    icon = painterResource(id = R.drawable.ic_checkmark_outline),
                    contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                    onClick = {
                        viewModel.apply {
                            if (scoutingType) {
                                setCompetitionMode(!competitionMode.value)
                                competitionMode.value = !competitionMode.value
                            } else {
                                setPitScoutingMode(!pitScoutingMode.value)
                                pitScoutingMode.value = !pitScoutingMode.value
                            }
                            showingScheduledScoutingModeDialog.value = false
                        }
                    },
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}