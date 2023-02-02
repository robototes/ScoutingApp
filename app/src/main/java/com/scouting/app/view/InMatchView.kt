package com.scouting.app.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.scouting.app.NavDestination
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.LabeledCounter
import com.scouting.app.components.LabeledRatingBar
import com.scouting.app.components.LabeledTriCounter
import com.scouting.app.components.SmallButton
import com.scouting.app.model.TemplateItem
import com.scouting.app.model.TemplateTypes
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
                        else -> AffirmativeGreen
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
        LazyColumn(
            modifier = Modifier.padding(top = 20.dp)
        ) {
            itemsIndexed(
                when (viewModel.currentMatchStage.value) {
                    0 -> viewModel.autoListItems
                    else -> viewModel.teleListItems
                }
            ) { _, item ->
                when (item.type) {
                    TemplateTypes.SCORE_BAR -> {
                        item.itemValueInt = remember { mutableStateOf(0) }
                        LabeledCounter(
                            text = item.text,
                            onValueChange = {
                                item.itemValueInt!!.value = it
                            },
                            incrementStep = 1,
                            modifier = Modifier.padding(30.dp)
                        )
                    }
                    TemplateTypes.CHECK_BOX -> {
                        item.itemValueBoolean = remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.padding(30.dp),
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
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                    }
                    TemplateTypes.PLAIN_TEXT -> {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(30.dp)
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
                                .padding(30.dp)
                        )
                    }
                    TemplateTypes.RATING_BAR -> {
                        item.itemValueInt = remember { mutableStateOf(0) }
                        LabeledRatingBar(
                            text = item.text,
                            values = 5,
                            onValueChange = { item.itemValueInt!!.value = it },
                            modifier = Modifier.padding(30.dp)
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
                }
            }
        }
    }
}
