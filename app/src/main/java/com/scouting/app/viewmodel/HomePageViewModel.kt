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
import com.scouting.app.utilities.getPreferences
import kotlinx.coroutines.launch

class HomePageViewModel : ViewModel() {

    var showingDeviceEditDialog by mutableStateOf(false)
    var showingTemplateTypeDialog by mutableStateOf(false)
    var deviceEditNameText by mutableStateOf(TextFieldValue())

    fun restoreDeviceName(context: MainActivity) {
        viewModelScope.launch {
            deviceEditNameText = TextFieldValue(
                context.getPreferences(MODE_PRIVATE).getString(
                    "CURRENT_SCOUT_NAME",
                    context.resources.getString(R.string.home_page_default_device_name)
                )!!
            )
        }
    }

    fun applyDeviceNameChange(context: MainActivity) {
        viewModelScope.launch {
            context.getPreferences(MODE_PRIVATE).edit().apply {
                putString("CURRENT_SCOUT_NAME", deviceEditNameText.text)
                apply()
            }
        }
    }

}