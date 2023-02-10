package com.scouting.app.view.scouting

import android.content.Context
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
import com.scouting.app.misc.MatchManager
import com.scouting.app.misc.NavDestination
import com.scouting.app.theme.AffirmativeGreen
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.ScoutingViewModel

@Composable
fun StartMatchView(navController: NavController, matchManager: MatchManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(ScoutingViewModel::class.java)
    val itemSpacing = 50.dp
    LaunchedEffect(true) {
        viewModel.apply {
            scoutingType.value = false
            loadTemplateItems(context)
            this.matchManager = matchManager
            populateMatchDataIfCompetition(context)
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
                        hint = viewModel.currentMatchMonitoring.value.text,
                        textFieldValue = viewModel.currentMatchMonitoring.value,
                        onValueChange = { newText ->
                            viewModel.currentMatchMonitoring.value = newText
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
                        hint = viewModel.currentTeamNumberMonitoring.value.text,
                        textFieldValue = viewModel.currentTeamNumberMonitoring.value,
                        onValueChange = { newText ->
                            viewModel.currentTeamNumberMonitoring.value = newText
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
                            viewModel.currentAllianceMonitoring.value = when (value) {
                                1 -> false
                                else -> true
                            }
                        },
                        customTextValues = listOf(
                            stringResource(id = R.string.start_match_alliance_label_red),
                            stringResource(id = R.string.start_match_alliance_label_blue)
                        ),
                        allianceSelectionColor = true,
                        startingSelectedIndex = if (viewModel.currentAllianceMonitoring.value) 1 else 0
                    )
                }
                LargeButton(
                    text = stringResource(id = R.string.start_match_begin_button_text),
                    icon = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                    onClick = {
                        if (context.getPreferences(Context.MODE_PRIVATE).getString(
                                "DEFAULT_TEMPLATE_FILE_PATH_MATCH", ""
                            )!!.isEmpty()
                        ) {
                            viewModel.showingNoTemplateDialog.value = true
                        } else {
                            viewModel.resetMatchConfig()
                            navController.navigate("${NavDestination.Scouting}/" + true)
                        }
                    },
                    color = AffirmativeGreen,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = itemSpacing)
                )
            }
        }
    }
    NoTemplateDialog(viewModel, navController)
}