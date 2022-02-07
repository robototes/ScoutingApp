package com.scorescape.app.scouting.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.components.LargeButton
import com.scorescape.app.scouting.utilities.getViewModel
import com.scorescape.app.scouting.view.dialog.DeviceNameDialog
import com.scorescape.app.scouting.view.dialog.TemplateTypeDialog
import com.scorescape.app.scouting.viewmodel.HomePageViewModel

@Composable
fun HomePageView(navController: NavController) {
    val viewModel = navController.context.getViewModel(HomePageViewModel::class.java)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            viewModel.showingDeviceEditDialog = true
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_avatar),
                        contentDescription = stringResource(id = R.string.ic_user_avatar_content_desc)
                    )
                    Text(
                        text = viewModel.deviceEditNameText.text,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = stringResource(id = R.string.ic_settings_content_desc),
                    modifier = Modifier.clickable {

                    }
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.1F))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.h1,
                fontSize = 80.sp
            )
            Text(
                text = stringResource(id = R.string.home_page_subtitle_text),
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.6F))
            LargeButton(
                text = stringResource(id = R.string.home_page_button_start_text),
                icon = painterResource(id = R.drawable.ic_play_button),
                contentDescription = stringResource(id = R.string.ic_play_button_content_desc),
                onClick = { /*TODO*/ },
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            LargeButton(
                text = stringResource(id = R.string.home_page_button_create_text),
                icon = painterResource(id = R.drawable.ic_server_wired),
                contentDescription = stringResource(id = R.string.ic_server_wired_content_desc),
                onClick = {
                     viewModel.showingTemplateTypeDialog = true
                },
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            LargeButton(
                text = stringResource(id = R.string.home_page_button_pit_text),
                icon = painterResource(id = R.drawable.ic_pit_stand),
                contentDescription = stringResource(id = R.string.ic_pit_stand_content_desc),
                onClick = { /*TODO*/ },
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
        LocalContext.current.apply {
            LaunchedEffect(key1 = true) {
                viewModel.restoreDeviceName(this@apply)
            }
        }
        DeviceNameDialog(viewModel = viewModel)
        TemplateTypeDialog(viewModel = viewModel, navController = navController)
    }
}


