package com.scouting.app.view.scouting

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.SmallButton
import com.scouting.app.components.SpacedRow
import com.scouting.app.misc.NavDestination
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.viewmodel.ScoutingViewModel

@Composable
fun NoTemplateDialog(navController: NavController, viewModel: ScoutingViewModel) {
    if (viewModel.showingNoTemplateDialog) {
        ScoutingTheme {
            DialogScaffold(
                icon = painterResource(id = R.drawable.ic_help),
                contentDescription = stringResource(id = R.string.ic_help_content_desc),
                title = stringResource(id = R.string.start_scouting_dialog_no_template_title),
                onDismissRequest = {
                    viewModel.showingNoTemplateDialog = false
                }
            ) {
                Text(
                    text = stringResource(id = R.string.start_scouting_dialog_no_template_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)
                )
                SpacedRow(modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)) {
                    SmallButton(
                        text = stringResource(id = R.string.start_scouting_dialog_no_template_neutral_button_text),
                        icon = painterResource(id = R.drawable.ic_settings),
                        contentDescription = stringResource(id = R.string.ic_settings_content_desc),
                        onClick = {
                            viewModel.showingNoTemplateDialog = false
                            navController.navigate(NavDestination.Settings)
                        },
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    SmallButton(
                        text = stringResource(id = R.string.start_scouting_dialog_no_template_positive_button_text),
                        icon = painterResource(id = R.drawable.ic_checkmark_outline),
                        contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                        onClick = {
                            viewModel.showingNoTemplateDialog = false
                        },
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}