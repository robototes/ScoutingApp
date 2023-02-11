package com.scouting.app.view.scouting

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.LargeButton
import com.scouting.app.components.LargeHeaderBar
import com.scouting.app.components.RatingBar
import com.scouting.app.components.SpacedRow
import com.scouting.app.misc.AllianceType
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.ScoutingType
import com.scouting.app.theme.AffirmativeGreen
import com.scouting.app.theme.AffirmativeGreenDark
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.ScoutingViewModel
import com.tencent.mmkv.MMKV

@Composable
fun StartMatchView(navController: NavController, scoutingScheduleManager: ScoutingScheduleManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(ScoutingViewModel::class.java)
    val itemSpacing = 50.dp
    LaunchedEffect(true) {
        viewModel.apply {
            scoutingType = ScoutingType.MATCH
            loadTemplateItems()
            this.scoutingScheduleManager = scoutingScheduleManager
            populateMatchDataIfCompetition()
        }
    }
    ScoutingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                LargeHeaderBar(
                    title = stringResource(id = R.string.start_match_header_title),
                    navController = navController
                )
                SpacedRow(modifier = Modifier.padding(top = itemSpacing)) {
                    Text(
                        text = stringResource(id = R.string.start_match_match_number_text),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    BasicInputField(
                        hint = viewModel.currentMatchMonitoring.text,
                        textFieldValue = viewModel.currentMatchMonitoring,
                        onValueChange = { newText ->
                            viewModel.currentMatchMonitoring = newText
                        },
                        icon = painterResource(id = R.drawable.ic_time),
                        modifier = Modifier.width(115.dp)
                    )
                }
                SpacedRow(modifier = Modifier.padding(top = itemSpacing)) {
                    Text(
                        text = stringResource(id = R.string.start_match_team_number_text),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    BasicInputField(
                        hint = viewModel.currentTeamNumberMonitoring.text,
                        textFieldValue = viewModel.currentTeamNumberMonitoring,
                        onValueChange = { newText ->
                            viewModel.currentTeamNumberMonitoring = newText
                        },
                        icon = painterResource(id = R.drawable.ic_machine_learning),
                        modifier = Modifier.width(125.dp)
                    )
                }
                SpacedRow(modifier = Modifier.padding(top = itemSpacing)) {
                    Text(
                        text = stringResource(id = R.string.start_match_alliance_selection_text),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    RatingBar(
                        values = 2,
                        onValueChange = { value ->
                            viewModel.currentAllianceMonitoring = when (value) {
                                1 -> AllianceType.RED
                                else -> AllianceType.BLUE
                            }
                        },
                        customTextValues = listOf(
                            stringResource(id = R.string.start_match_alliance_label_red),
                            stringResource(id = R.string.start_match_alliance_label_blue)
                        ),
                        allianceSelectionColor = true,
                        startingSelectedIndex = if (
                            viewModel.currentAllianceMonitoring == AllianceType.RED
                        ) 1 else 0
                    )
                }
                LargeButton(
                    text = stringResource(id = R.string.start_match_begin_button_text),
                    icon = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                    onClick = {
                        if (MMKV.defaultMMKV()
                                .decodeString("DEFAULT_TEMPLATE_FILE_PATH_MATCH", "")!!.isEmpty()
                        ) {
                            viewModel.showingNoTemplateDialog = true
                        } else if (
                            viewModel.currentMatchMonitoring.text.isBlank() ||
                            viewModel.currentTeamNumberMonitoring.text.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.start_match_fill_out_fields_toast_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.resetMatchConfig()
                            navController.navigate("${NavDestination.Scouting}/" + true)
                        }
                    },
                    color = AffirmativeGreen,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = itemSpacing),
                    colorBorder = AffirmativeGreenDark
                )
            }
        }
    }
    NoTemplateDialog(viewModel, navController)
}