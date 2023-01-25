package com.scouting.app.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.NavDestination
import com.scouting.app.R
import com.scouting.app.components.LargeButton
import com.scouting.app.theme.NeutralGrayMedium
import com.scouting.app.viewmodel.HomePageViewModel
import com.scouting.app.components.DialogScaffold

@Composable
fun TemplateTypeDialog(
    viewModel: HomePageViewModel,
    navController: NavController
) {
    if (viewModel.showingTemplateTypeDialog) {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_server_wired),
            contentDescription = stringResource(id = R.string.ic_server_wired_content_desc),
            title = stringResource(id = R.string.home_page_template_type_dialog_title),
            onDismissRequest = {
                viewModel.showingTemplateTypeDialog = false
            }
        ) {
            Column(
                modifier = Modifier.padding(30.dp)
            ) {
                LargeButton(
                    text = stringResource(id = R.string.home_page_template_type_dialog_match),
                    onClick = {
                        navController.navigate(NavDestination.CreateTemplateConfig)
                    },
                    color = NeutralGrayMedium,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                LargeButton(
                    text = stringResource(id = R.string.home_page_template_type_dialog_pit),
                    onClick = {
                        navController.navigate("${NavDestination.TemplateEditor}/pit")
                    },
                    color = NeutralGrayMedium
                )
            }
        }
    }
}