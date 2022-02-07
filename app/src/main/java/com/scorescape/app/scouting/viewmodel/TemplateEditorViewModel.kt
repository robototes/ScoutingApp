package com.scorescape.app.scouting.viewmodel

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scorescape.app.scouting.model.TemplateFormatMatch
import com.scorescape.app.scouting.model.TemplateFormatPit
import com.scorescape.app.scouting.model.TemplateItem
import org.json.JSONArray
import java.io.File

class TemplateEditorViewModel : ViewModel() {

    var currentTemplateType = "match"

    var gameNameTextValue by mutableStateOf(TextFieldValue())
    var gameYearTextValue by mutableStateOf(TextFieldValue())
    var finalFileName by mutableStateOf(TextFieldValue("${gameNameTextValue.text}${gameYearTextValue.text}"))

    var autoDuration by mutableStateOf(0)
    var teleOpDuration by mutableStateOf(0)
    var endgameDuration by mutableStateOf(0)

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var endgameListItems = mutableStateListOf<TemplateItem>()
    var pitListItems = mutableStateListOf<TemplateItem>()

    var showingEditDialog by mutableStateOf(false)

    var currentListResource by mutableStateOf(pitListItems)
    var currentEditItemIndex by mutableStateOf(0)

    fun exportTemplateToJSON() {
        val template: Any = if (currentTemplateType == "match") {
            TemplateFormatMatch(
                title = finalFileName.text,
                autoTemplateItems = autoListItems,
                teleTemplateItems = teleListItems,
                endTemplateItems = endgameListItems,
                autoDuration = autoDuration,
                teleDuration = teleOpDuration,
                endDuration = endgameDuration
            )
        } else {
            TemplateFormatPit(
                title = finalFileName.text,
                templateItems = pitListItems
            )
        }
        Log.e("TAG", Gson().toJson(template))
    }

}