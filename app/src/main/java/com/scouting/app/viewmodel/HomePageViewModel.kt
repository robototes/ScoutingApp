package com.scouting.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomePageViewModel : ViewModel() {

    var showingTemplateTypeDialog by mutableStateOf(false)

}