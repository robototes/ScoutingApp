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
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.SmallButton
import com.scouting.app.components.SpacedRow
import com.scouting.app.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV

@Composable
fun CompetitionModeDialog(viewModel: SettingsViewModel, navController: NavController) {
    val competitionMode = MMKV.defaultMMKV().decodeBool("COMPETITION_MODE", false)
    if (viewModel.showingCompetitionModeDialog.value) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_rotate_180),
            contentDescription = stringResource(id = R.string.ic_rotate_180_content_desc),
            title = if (competitionMode) {
                stringResource(id = R.string.settings_competition_mode_dialog_disable_title)
            } else {
                stringResource(id = R.string.settings_competition_mode_dialog_enable_title)
            },
            onDismissRequest = {
                viewModel.showingCompetitionModeDialog.value = false
            }
        ) {
            Text(
                text = if (competitionMode) {
                    stringResource(id = R.string.settings_competition_mode_dialog_disable_subtitle)
                } else {
                    stringResource(id = R.string.settings_competition_mode_dialog_enable_subtitle)
                },
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)
            )
            SpacedRow(modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)) {
                SmallButton(
                    text = stringResource(id = R.string.settings_competition_mode_dialog_negative_button_label),
                    icon = painterResource(id = R.drawable.ic_close_outline),
                    contentDescription = stringResource(id = R.string.ic_close_outline_content_desc),
                    onClick = {
                        viewModel.showingCompetitionModeDialog.value = false
                    },
                    color = MaterialTheme.colorScheme.error
                )
                SmallButton(
                    text = stringResource(id = R.string.settings_competition_mode_dialog_positive_button_label),
                    icon = painterResource(id = R.drawable.ic_checkmark_outline),
                    contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                    onClick = {
                        viewModel.apply {
                            setCompetitionMode(!competitionMode)
                            viewModel.competitionMode.value = !competitionMode
                            showingCompetitionModeDialog.value = false
                        }
                    },
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}