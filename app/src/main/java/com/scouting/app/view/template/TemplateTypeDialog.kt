package com.scouting.app.view.template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.LargeButton
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.RequestCode
import com.scouting.app.theme.NeutralGrayDark
import com.scouting.app.theme.NeutralGrayMedium
import com.scouting.app.utilities.composableContext
import com.scouting.app.viewmodel.HomePageViewModel

@Composable
fun TemplateTypeDialog(navController: NavController, viewModel: HomePageViewModel) {
    val context = composableContext
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
                        navController.navigate("${NavDestination.TemplateEditor}/MATCH")
                        viewModel.showingTemplateTypeDialog = false
                    },
                    color = NeutralGrayMedium,
                    modifier = Modifier.padding(bottom = 15.dp),
                    colorBorder = NeutralGrayDark
                )
                LargeButton(
                    text = stringResource(id = R.string.home_page_template_type_dialog_pit),
                    onClick = {
                        navController.navigate("${NavDestination.TemplateEditor}/PIT")
                        viewModel.showingTemplateTypeDialog = false
                    },
                    color = NeutralGrayMedium,
                    colorBorder = NeutralGrayDark,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                LargeButton(
                    text = stringResource(id = R.string.home_page_template_type_dialog_import),
                    onClick = {
                        context.requestFilePicker(
                            code = RequestCode.TEMPLATE_EDITOR_IMPORT_FILE_PICK,
                            type = arrayOf("json")
                        )
                        viewModel.showingTemplateTypeDialog = false
                    },
                    color = NeutralGrayMedium,
                    colorBorder = NeutralGrayDark
                )
            }
        }
    }
}