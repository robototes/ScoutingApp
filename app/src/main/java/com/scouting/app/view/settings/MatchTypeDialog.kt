package com.scouting.app.view.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.DialogScaffold
import com.scouting.app.components.SmallButton
import com.scouting.app.viewmodel.SettingsViewModel

@Composable
fun MatchTypeDialog(viewModel: SettingsViewModel) {
    if (!viewModel.showingMatchTypeDialog) {
        return
    }
    DialogScaffold(icon = painterResource(id = R.drawable.ic_edit_pen),
        contentDescription = stringResource(R.string.ic_edit_pen_content_desc),
        title = stringResource(id = R.string.settings_choose_match_type_dialog_title),
        onDismissRequest = {
            viewModel.apply {
                showingMatchTypeDialog = false
                restoreMatchType()
            }
        }
    ) {
        Column {
            BasicInputField(
                icon = painterResource(id = R.drawable.ic_edit_pen),
                contentDescription = stringResource(id = R.string.ic_edit_pen_content_desc),
                hint = stringResource(id = R.string.settings_choose_match_type_dialog_input_title),
                textFieldValue = viewModel.matchType,
                onValueChange = { value ->
                    viewModel.apply {
                        matchType = value
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            )
            SmallButton(
                text = stringResource(id = R.string.home_page_device_edit_dialog_save_button),
                icon = painterResource(id = R.drawable.ic_checkmark_outline),
                contentDescription = stringResource(id = R.string.ic_checkmark_outline_content_desc),
                onClick = {
                    viewModel.apply {
                        showingMatchTypeDialog = false
                        applyMatchTypeChange(matchType.text)
                        // Reset text field value
                        matchType = TextFieldValue(matchType.text)
                    }
                },
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 25.dp)
            )
        }
    }
}