package com.scouting.app.view.scouting

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.scouting.app.components.SpacedRow
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.ScoutingType
import com.scouting.app.theme.AffirmativeGreen
import com.scouting.app.theme.AffirmativeGreenDark
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.ScoutingViewModel

@Composable
fun StartPitScoutingView(navController: NavController, scoutingScheduleManager: ScoutingScheduleManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(ScoutingViewModel::class.java)
    LaunchedEffect(true) {
        viewModel.apply {
            this.scoutingScheduleManager = scoutingScheduleManager
            populatePitDataIfScheduled()
        }
    }
    ScoutingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                LargeHeaderBar(
                    title = stringResource(id = R.string.start_pit_scouting_header_title),
                    navController = navController
                )
                SpacedRow(modifier = Modifier.padding(top = 50.dp)) {
                    Text(
                        text = stringResource(id = R.string.start_pit_scouting_team_number_prefix),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    BasicInputField(
                        hint = viewModel.currentTeamNumberMonitoring.text,
                        textFieldValue = viewModel.currentTeamNumberMonitoring,
                        onValueChange = { newText ->
                            viewModel.currentTeamNumberMonitoring = newText
                        },
                        icon = painterResource(id = R.drawable.ic_list_numbered),
                        modifier = Modifier.width(130.dp),
                        numberKeyboard = true
                    )
                }
                SpacedRow(modifier = Modifier.padding(top = 50.dp)) {
                    Text(
                        text = stringResource(id = R.string.start_pit_scouting_team_name_prefix),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    BasicInputField(
                        hint = viewModel.currentTeamNameMonitoring.text,
                        textFieldValue = viewModel.currentTeamNameMonitoring,
                        onValueChange = { newText ->
                            viewModel.currentTeamNameMonitoring = newText
                        },
                        icon = painterResource(id = R.drawable.ic_machine_learning)
                    )
                }
                LargeButton(
                    text = stringResource(id = R.string.start_pit_scouting_begin_button_text),
                    icon = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                    onClick = {
                        var loadingFailed: Boolean
                        viewModel.apply {
                            scoutingType = ScoutingType.PIT
                            loadingFailed = loadTemplateItems()
                        }
                        if (loadingFailed) {
                            viewModel.showingNoTemplateDialog = true
                        } else if (
                            viewModel.currentTeamNameMonitoring.text.isBlank() ||
                            viewModel.currentTeamNumberMonitoring.text.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.start_match_fill_out_fields_toast_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            navController.navigate("${NavDestination.Scouting}/" + false)
                        }
                    },
                    color = AffirmativeGreen,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 50.dp),
                    colorBorder = AffirmativeGreenDark
                )
            }
        }
    }
    NoTemplateDialog(viewModel, navController)
}