package com.scouting.app.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.NavDestination
import com.scouting.app.R
import com.scouting.app.components.SmallButton
import com.scouting.app.model.TemplateItem
import com.scouting.app.theme.AffirmativeGreen
import com.scouting.app.theme.ErrorRed
import com.scouting.app.theme.NeutralGrayDark
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.theme.NeutralGrayMedium
import com.scouting.app.theme.PrimaryBlue
import com.scouting.app.theme.SecondaryPurple
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.InMatchViewModel

@Composable
fun InMatchView(navController: NavController) {
    val viewModel = LocalContext.current.getViewModel(InMatchViewModel::class.java)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = String.format(
                        stringResource(id = R.string.in_match_header_match_number_format),
                        viewModel.currentMatchMonitoring.value.text
                    ),
                    style = MaterialTheme.typography.h4
                )
                Text(
                    text = String.format(
                        stringResource(id = R.string.in_match_header_team_number_format),
                        viewModel.currentTeamMonitoring.value.text
                    ),
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = if (viewModel.currentAllianceMonitoring.value) {
                        stringResource(id = R.string.in_match_header_alliance_blue)
                    } else {
                        stringResource(id = R.string.in_match_header_alliance_red)
                    },
                    style = MaterialTheme.typography.h6,
                    color = if (viewModel.currentAllianceMonitoring.value) {
                        Color.Blue
                    } else {
                        Color.Red
                    }
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = 0.dp,
                    backgroundColor = when (viewModel.currentMatchStage.value) {
                        0 -> SecondaryPurple
                        1 -> AffirmativeGreen
                        else -> ErrorRed
                    }
                ) {
                    Text(
                        text = viewModel.let {
                            it.getCorrespondingMatchStageName(
                                matchStage = it.currentMatchStage.value
                            )
                        },
                        style = MaterialTheme.typography.h5
                                + TextStyle(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )
                }
                SmallButton(
                    text = viewModel.let {
                        if (it.currentMatchStage.value >= 2) {
                            stringResource(id = R.string.in_match_stage_finish_scout_text)
                        } else {
                            String.format(
                                stringResource(id = R.string.in_match_stage_move_on_format),
                                viewModel.getCorrespondingMatchStageName(
                                    matchStage = it.currentMatchStage.value + 1
                                )
                            )
                        }
                    },
                    icon = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                    onClick = {
                        viewModel.currentMatchStage.let {
                            if (it.value >= 2) {
                                navController.navigate(NavDestination.FinishMatch)
                            } else {
                                it.value++
                            }
                        }
                    },
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(top = 15.dp),
                    outlineStyle = true
                )
            }
        }
        TemplateLoadView(
            template = viewModel.let {
                when (it.currentMatchStage.value) {
                    0 -> it.autoListItems
                    1 -> it.teleListItems
                    else -> it.endgameListItems
                }
            }
        )
    }
}
