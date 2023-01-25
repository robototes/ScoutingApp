package com.scouting.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.scouting.app.R
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.TemplateEditorViewModel
import com.scouting.app.components.MediumHeaderBar
import com.scouting.app.components.TabLayout
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalPagerApi::class)
fun TemplatePreviewView(navController: NavController) {
    val viewModel = navController.context.getViewModel(TemplateEditorViewModel::class.java)
    val pagerState = rememberPagerState()
    viewModel.apply {
        if (currentTemplateType == "pit")
            rememberCoroutineScope().launch {
                pagerState.animateScrollToPage(3)
            }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MediumHeaderBar(
            title = stringResource(id = R.string.template_preview_header_title),
            navController = navController
        )
        if (viewModel.currentTemplateType == "match") {
            TabLayout(
                items = listOf(
                    stringResource(id = R.string.template_editor_auto_header),
                    stringResource(id = R.string.template_editor_tele_header),
                    stringResource(id = R.string.template_editor_endgame_header)
                ),
                selection = viewModel.currentSelectedTab,
                onSelectionChange = {
                    viewModel.apply {
                        currentSelectedTab.value = it
                    }
                },
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            TemplateLoadView(template = when (page){
                0 -> viewModel.autoListItems
                1 -> viewModel.teleListItems
                2 -> viewModel.endgameListItems
                else -> viewModel.pitListItems
            })
        }
    }
}