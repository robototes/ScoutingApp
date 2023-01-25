package com.scouting.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R
import com.scouting.app.components.MediumButton
import com.scouting.app.theme.NeutralGrayLight
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.InMatchViewModel
import com.scouting.app.components.LargeHeaderBar

@Composable
fun StartMatchView(navController: NavController) {
    val viewModel = navController.context.getViewModel(InMatchViewModel::class.java)
    Surface {
        Column {
            LargeHeaderBar(
                title = stringResource(id = R.string.start_match_header_title),
                navController = navController
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 32.dp, end = 30.dp, top = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.start_match_choose_template_title),
                    style = MaterialTheme.typography.body2
                )
                MediumButton(
                    text = "file.json",
                    icon = painterResource(id = R.drawable.ic_save_file),
                    contentDescription = stringResource(id = R.string.ic_save_file_content_desc),
                    onClick = { /*TODO*/ },
                    color = NeutralGrayLight
                )
            }
        }
    }
}