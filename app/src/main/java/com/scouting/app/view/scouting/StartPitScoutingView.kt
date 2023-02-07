package com.scouting.app.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.scouting.app.utilities.getViewModel
import com.scouting.app.viewmodel.InMatchViewModel

@Composable
fun StartPitScoutingView(navController: NavController) {
    val viewModel = navController.context.getViewModel(InMatchViewModel::class.java)
    LaunchedEffect(true) {
        viewModel.apply {
            scoutingType.value = true
        }
    }
}