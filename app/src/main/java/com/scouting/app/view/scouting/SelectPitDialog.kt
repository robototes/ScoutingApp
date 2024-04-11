package com.scouting.app.view.scouting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scouting.app.R
import com.scouting.app.components.DialogScaffold
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.viewmodel.ScoutingViewModel

@Composable
fun SelectPitDialog(scoutingScheduleManager: ScoutingScheduleManager, viewModel: ScoutingViewModel) {
    if (!viewModel.showingSelectPitDialog) {
        return
    }
    ScoutingTheme {
        DialogScaffold(
            icon = painterResource(id = R.drawable.ic_calendar_panel),
            contentDescription = stringResource(id = R.string.ic_calendar_panel_content_desc),
            title = stringResource(id = R.string.start_scouting_dialog_select_pit_title),
            onDismissRequest = {
                viewModel.showingSelectPitDialog = false
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .heightIn(0.dp, 500.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                itemsIndexed(scoutingScheduleManager.currentPitScheduleCSV) { index, item ->
                    val number = item[0]
                    val teamName = item[1]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                            .background(color = NeutralGrayLight, shape = MaterialTheme.shapes.medium)
                            .clickable {
                                scoutingScheduleManager.jumpToPit(index)
                                viewModel.populatePitDataIfScheduled()
                                viewModel.showingSelectPitDialog = false
                            },
                        horizontalArrangement = Arrangement.Start
                    ) {
                        MaterialTheme.typography.bodyLarge.color
                        Text(
                            text = number,
                            modifier = Modifier
                                .padding(15.dp)
                                .width(70.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = teamName,
                            modifier = Modifier.padding(15.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
