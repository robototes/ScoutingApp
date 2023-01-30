package com.scouting.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.LargeHeaderBar
import com.scouting.app.components.MediumButton
import com.scouting.app.components.SettingsPreference
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.SettingsViewModel

@Composable
fun SettingsView(navController: NavController) {
    val context = navController.context
    val viewModel = context.getViewModel(SettingsViewModel::class.java)
    ScoutingTheme {
        Column {
            LargeHeaderBar(
                title = stringResource(id = R.string.settings_header_title),
                navController = navController
            )
            Column(modifier = Modifier.padding(top = 20.dp)) {
                SettingsPreference(
                    title = stringResource(id = R.string.settings_choose_default_template_title),
                    subtitle = stringResource(id = R.string.settings_choose_default_template_subtitle),
                    endContent = {
                        MediumButton(
                            text = viewModel.defaultTemplateFileName.value,
                            icon = painterResource(id = R.drawable.ic_save_file),
                            contentDescription = stringResource(id = R.string.ic_save_file_content_desc),
                            onClick = { viewModel.requestFilePicker(context as MainActivity) },
                            color = NeutralGrayLight
                        )
                    }
                )
            }
        }
    }
}