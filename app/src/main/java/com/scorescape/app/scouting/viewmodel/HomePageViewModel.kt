package com.scorescape.app.scouting.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.utilities.getPreferences
import kotlinx.coroutines.launch

class HomePageViewModel : ViewModel() {

    var showingDeviceEditDialog by mutableStateOf(false)
    var showingTemplateTypeDialog by mutableStateOf(false)
    var deviceEditNameText by mutableStateOf(TextFieldValue())

    fun restoreDeviceName(context: Context) {
        viewModelScope.launch {
            deviceEditNameText = TextFieldValue(
                context.getPreferences().getString(
                    "CURRENT_SCOUT_NAME",
                    context.resources.getString(R.string.home_page_default_device_name)
                )!!
            )
        }
    }

    fun applyDeviceNameChange(context: Context) {
        viewModelScope.launch {
            context.getPreferences().edit().apply {
                putString("CURRENT_SCOUT_NAME", deviceEditNameText.text)
                apply()
            }
        }
    }

}