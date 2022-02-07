package com.scorescape.app.scouting.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scorescape.app.scouting.NavDestination
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.components.*
import com.scorescape.app.scouting.utilities.getViewModel
import com.scorescape.app.scouting.viewmodel.TemplateEditorViewModel

@Composable
fun TemplateConfigView(navController: NavController) {
    val viewModel = navController.context.getViewModel(TemplateEditorViewModel::class.java)
    Surface {
        Column {
            LargeHeaderBar(
                title = stringResource(id = R.string.template_config_header_title),
                navController = navController
            )
            BasicInputField(
                icon = painterResource(id = R.drawable.ic_edit_pen),
                contentDescription = stringResource(id = R.string.ic_edit_pen_content_desc),
                hint = stringResource(id = R.string.template_config_game_name_hint),
                textFieldValue = viewModel.gameNameTextValue,
                onValueChange = {
                    viewModel.gameNameTextValue = it
                },
                modifier = Modifier
                    .padding(start = 30.dp, end = 40.dp, top = 35.dp, bottom = 20.dp)
                    .fillMaxWidth()
            )
            BasicInputField(
                icon = painterResource(id = R.drawable.ic_calendar_panel),
                contentDescription = stringResource(id = R.string.ic_calendar_panel_content_desc),
                hint = stringResource(id = R.string.template_config_game_year_hint),
                textFieldValue = viewModel.gameYearTextValue,
                onValueChange = {
                    viewModel.gameYearTextValue = it
                },
                modifier = Modifier.padding(start = 30.dp)
            )
            Column(
                modifier = Modifier.padding(start = 30.dp, end = 40.dp, top = 45.dp)
            ) {
                LabeledCounter(
                    text = stringResource(id = R.string.template_config_auto_duration),
                    onValueChange = {
                        viewModel.autoDuration = it
                    },
                    incrementStep = 5,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
                LabeledCounter(
                    text = stringResource(id = R.string.template_config_tele_duration),
                    onValueChange = {
                        viewModel.teleOpDuration = it
                    },
                    incrementStep = 5,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
                LabeledCounter(
                    text = stringResource(id = R.string.template_config_end_duration),
                    onValueChange = {
                        viewModel.endgameDuration = it
                    },
                    incrementStep = 5,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, end = 40.dp),
                horizontalArrangement = Arrangement.End
            ) {
                MediumButton(
                    text = stringResource(id = R.string.template_config_start_editing_button),
                    icon = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                    onClick = {
                         navController.navigate("${NavDestination.TemplateEditor}/match")
                    },
                    color = MaterialTheme.colors.primaryVariant
                )
            }
        }
    }
}