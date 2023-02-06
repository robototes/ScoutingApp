package com.scouting.app.viewmodel

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouting.app.MainActivity
import com.scouting.app.R
import kotlinx.coroutines.launch

class HomePageViewModel : ViewModel() {

    var showingTemplateTypeDialog by mutableStateOf(false)

}