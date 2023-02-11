package com.scouting.app.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.LargeButton
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.NavDestination
import com.scouting.app.theme.AffirmativeGreenDark
import com.scouting.app.theme.ErrorRedDark
import com.scouting.app.theme.SecondaryPurpleDark
import com.scouting.app.utilities.getViewModel
import com.scouting.app.view.settings.DevicePositionDialog
import com.scouting.app.view.template.TemplateTypeDialog
import com.scouting.app.viewmodel.HomePageViewModel
import com.scouting.app.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV

@Composable
fun HomePageView(navController: NavController, scoutingScheduleManager: ScoutingScheduleManager) {
    val context = navController.context as MainActivity
    val viewModel = context.getViewModel(HomePageViewModel::class.java)
    val settingsViewModel =
        context.getViewModel(SettingsViewModel::class.java) // might be bad practice lolz
    val preferences = MMKV.defaultMMKV()
    LaunchedEffect(true) {
        settingsViewModel.loadSavedPreferences()
    }
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 35.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 5.dp, end = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val allianceColor = if (settingsViewModel.deviceAlliancePosition == "RED") {
                    Color(0xFFEC4076)
                } else {
                    Color(0xFF4284F5)
                }
                Button(
                    onClick = {
                        settingsViewModel.showingDevicePositionDialog = true
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = 2.5.dp,
                            color = allianceColor,
                            shape = MaterialTheme.shapes.medium
                        ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = preferences.let {
                            "${settingsViewModel.deviceAlliancePosition} ${settingsViewModel.deviceRobotPosition}"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = allianceColor
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate(NavDestination.Settings)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = stringResource(id = R.string.ic_settings_content_desc),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.1F))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 80.sp
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.home_page_subtitle_text))
                            addStyle(
                                start = 11,
                                end = 20,
                                style = SpanStyle(
                                    fontFamily = FontFamily(Font(R.font.bankgothic_medium)),
                                    letterSpacing = (-1).sp,
                                    fontSize = 28.sp
                                )
                            )
                            // Bring the "12" of 2412 closer together because it looks
                            // kind of like there is a space between them otherwise
                            addStyle(
                                start = 18,
                                end = 20,
                                style = SpanStyle(letterSpacing = (-3).sp)
                            )
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (preferences.decodeBool("PIT_SCOUTING_MODE", false)) {
                        Card(
                            shape =  MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            modifier = Modifier.padding(top = 25.dp)
                                .border(
                                    width = 2.dp,
                                    shape = MaterialTheme.shapes.large,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                        ) {
                            Text(
                                text = String.format(
                                    stringResource(id = R.string.home_page_pit_scouting_status_chip_format),
                                    scoutingScheduleManager.getPitsLeftToScout()
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                            )
                        }
                    }
                }
                Column {
                    LargeButton(
                        text = if (preferences.decodeBool("COMPETITION_MODE", false)) {
                            String.format(
                                stringResource(id = R.string.home_page_button_competition_start_format),
                                scoutingScheduleManager.currentMatchScoutingIteration + 1
                            )
                        } else {
                            stringResource(id = R.string.home_page_button_start_text)
                        },
                        icon = painterResource(id = R.drawable.ic_play_button),
                        contentDescription = stringResource(id = R.string.ic_play_button_content_desc),
                        onClick = {
                            navController.navigate(NavDestination.StartMatchScouting)
                        },
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 20.dp),
                        colorBorder = AffirmativeGreenDark
                    )
                    LargeButton(
                        text = if (preferences.decodeBool("PIT_SCOUTING_MODE", false)) {
                            String.format(
                                stringResource(id = R.string.home_page_button_pit_start_format),
                                scoutingScheduleManager.getCurrentPitInfo().first
                            )
                        } else {
                            stringResource(id = R.string.home_page_button_pit_text)
                        },
                        icon = painterResource(id = R.drawable.ic_pit_stand),
                        contentDescription = stringResource(id = R.string.ic_pit_stand_content_desc),
                        onClick = { navController.navigate(NavDestination.StartPitScouting) },
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 20.dp),
                        colorBorder = SecondaryPurpleDark
                    )
                    LargeButton(
                        text = stringResource(id = R.string.home_page_button_create_text),
                        icon = painterResource(id = R.drawable.ic_server_wired),
                        contentDescription = stringResource(id = R.string.ic_server_wired_content_desc),
                        onClick = {
                            viewModel.showingTemplateTypeDialog = true
                        },
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 20.dp),
                        colorBorder = ErrorRedDark
                    )
                }
            }
        }
        TemplateTypeDialog(viewModel, navController)
        DevicePositionDialog(
            viewModel = settingsViewModel,
            navController = navController
        )
    }
}


