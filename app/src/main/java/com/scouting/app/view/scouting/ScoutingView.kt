package com.scouting.app.view.scouting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.LabeledCounter
import com.scouting.app.components.LabeledRatingBar
import com.scouting.app.components.LabeledTriCounter
import com.scouting.app.components.SmallButton
import com.scouting.app.components.SpacedRow
import com.scouting.app.components.TriButtonBlock
import com.scouting.app.misc.AllianceType
import com.scouting.app.misc.MatchStage
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateItem
import com.scouting.app.theme.AffirmativeGreen
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.theme.SecondaryPurple
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.ScoutingViewModel

@Composable
fun ScoutingView(navController: NavController, scoutingMatch: Boolean) {
    val viewModel = LocalContext.current.getViewModel(ScoutingViewModel::class.java)
    ScoutingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                if (scoutingMatch) {
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
                                    viewModel.currentMatchMonitoring.text
                                ),
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = String.format(
                                    stringResource(id = R.string.in_match_header_team_number_format),
                                    viewModel.currentTeamNumberMonitoring.text
                                ),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = if (viewModel.currentAllianceMonitoring == AllianceType.BLUE) {
                                    stringResource(id = R.string.in_match_header_alliance_blue)
                                } else {
                                    stringResource(id = R.string.in_match_header_alliance_red)
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (viewModel.currentAllianceMonitoring == AllianceType.BLUE) {
                                    Color.Blue
                                } else {
                                    Color.Red
                                }
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(0.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (viewModel.currentMatchStage == MatchStage.AUTO) {
                                        SecondaryPurple
                                    } else {
                                        AffirmativeGreen
                                    }
                                )
                            ) {
                                Text(
                                    text = viewModel.currentMatchStage.name,
                                    style = MaterialTheme.typography.headlineMedium
                                            + TextStyle(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.padding(
                                        horizontal = 15.dp,
                                        vertical = 10.dp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            SmallButton(
                                text = viewModel.let {
                                    if (it.currentMatchStage == MatchStage.TELEOP) {
                                        stringResource(id = R.string.in_match_stage_finish_scout_text)
                                    } else {
                                        stringResource(id = R.string.in_match_stage_move_on)
                                    }
                                },
                                icon = painterResource(id = R.drawable.ic_arrow_forward),
                                contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                                onClick = {
                                    viewModel.let {
                                        if (it.currentMatchStage == MatchStage.TELEOP) {
                                            navController.navigate(NavDestination.FinishScouting)
                                        } else {
                                            it.currentMatchStage = MatchStage.TELEOP
                                        }
                                    }
                                },
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 15.dp),
                                outlineStyle = true
                            )
                        }
                    }
                } else {
                    SpacedRow(modifier = Modifier.padding(vertical = 20.dp)) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.in_pit_scouting_header_text),
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = String.format(
                                    stringResource(id = R.string.in_pit_scouting_primary_subtitle_text),
                                    viewModel.currentTeamNumberMonitoring.text
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(top = 5.dp)
                            )
                            Text(
                                text = viewModel.currentTeamNameMonitoring.text,
                                style = MaterialTheme.typography.headlineSmall +
                                        TextStyle(fontWeight = FontWeight.Normal)
                            )
                        }
                        SmallButton(
                            text = stringResource(id = R.string.in_pit_scouting_end_button_text),
                            icon = painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                            onClick = {
                                navController.navigate(NavDestination.FinishScouting)
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 15.dp),
                            outlineStyle = true
                        )
                    }
                }
                if (scoutingMatch) {
                    // Must have two different compositions of ScoutingTemplateLoadView
                    // because otherwise, compose will be "smart" and recycle the same
                    // views if at the same index the same view is going to exist (this
                    // is good behavior of LazyColumn) but in our case we want to recompose
                    // it so that the value of say, a counter is not persisted through
                    // match stage changes, confusing the user and messing up our data
                    AnimatedVisibility(visible = viewModel.currentMatchStage == MatchStage.AUTO) {
                        ScoutingTemplateLoadView(list = viewModel.autoListItems)
                    }
                    AnimatedVisibility(visible = viewModel.currentMatchStage == MatchStage.TELEOP) {
                        ScoutingTemplateLoadView(list = viewModel.teleListItems)
                    }
                } else {
                    ScoutingTemplateLoadView(list = viewModel.pitListItems)
                }
            }
        }
    }
}

@Composable
fun ScoutingTemplateLoadView(list: SnapshotStateList<TemplateItem>) {
    LazyColumn(
        modifier = Modifier.padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(45.dp)
    ) {
        itemsIndexed(list) { _, item ->
            when (item.type) {
                TemplateTypes.SCORE_BAR -> {
                    item.itemValueInt = remember { mutableStateOf(0) }
                    LabeledCounter(
                        text = item.text,
                        onValueChange = {
                            item.itemValueInt!!.value = it
                        },
                        incrementStep = 1,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    )
                }

                TemplateTypes.CHECK_BOX -> {
                    item.itemValueBoolean = remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                item.itemValueBoolean!!.let { it.value = !it.value }
                            }
                            .padding(start = 15.dp, end = 30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.itemValueBoolean!!.value,
                            onCheckedChange = {
                                item.itemValueBoolean!!.value = it
                            }
                        )
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                }

                TemplateTypes.PLAIN_TEXT -> {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    )
                }

                TemplateTypes.TEXT_FIELD -> {
                    var tempItemState by remember { mutableStateOf(TextFieldValue()) }
                    item.itemValueString = remember { mutableStateOf("") }
                    BasicInputField(
                        icon = painterResource(id = R.drawable.ic_text_format_center),
                        contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                        hint = item.text,
                        textFieldValue = tempItemState,
                        onValueChange = {
                            tempItemState = it
                            item.itemValueString!!.value = it.text
                        },
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }

                TemplateTypes.RATING_BAR -> {
                    item.itemValueInt = remember { mutableStateOf(0) }
                    LabeledRatingBar(
                        text = item.text,
                        values = 5,
                        onValueChange = { item.itemValueInt!!.value = it },
                        modifier = Modifier.padding(horizontal = 30.dp)
                    )
                }

                TemplateTypes.TRI_SCORING -> {
                    item.itemValueInt = remember { mutableStateOf(0) }
                    item.itemValue2Int = remember { mutableStateOf(0) }
                    item.itemValue3Int = remember { mutableStateOf(0) }
                    LabeledTriCounter(
                        text1 = item.text,
                        text2 = item.text2.toString(),
                        text3 = item.text3.toString(),
                        onValueChange1 = { item.itemValueInt!!.value = it },
                        onValueChange2 = { item.itemValue2Int!!.value = it },
                        onValueChange3 = { item.itemValue3Int!!.value = it }
                    )
                }

                TemplateTypes.TRI_BUTTON -> {
                    item.itemValueInt = remember { mutableStateOf(0) }
                    TriButtonBlock(
                        headerText = item.text,
                        buttonLabelOne = item.text2.toString(),
                        buttonLabelTwo = item.text3.toString(),
                        buttonLabelThree = item.text4.toString(),
                        onValueChange = {
                            item.itemValueInt!!.value = it
                        },
                        modifier = Modifier.padding(
                            bottom = 10.dp,
                            start = 30.dp,
                            end = 30.dp,
                        )
                    )
                }
            }
        }
    }
}