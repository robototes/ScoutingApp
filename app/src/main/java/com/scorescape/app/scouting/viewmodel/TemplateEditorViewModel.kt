package com.scorescape.app.scouting.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scorescape.app.scouting.MainActivity
import com.scorescape.app.scouting.model.TemplateFormatMatch
import com.scorescape.app.scouting.model.TemplateFormatPit
import com.scorescape.app.scouting.model.TemplateItem
import java.io.OutputStream

class TemplateEditorViewModel : ViewModel() {

    var currentTemplateType = "match"

    var gameNameTextValue by mutableStateOf(TextFieldValue())
    var gameYearTextValue by mutableStateOf(TextFieldValue())
    var finalFileName by mutableStateOf(
        TextFieldValue("${gameNameTextValue.text}${gameYearTextValue.text}.json")
    )

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

    fun requestFilePicker(context: MainActivity) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, finalFileName.text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("~/Documents"))
            }
        }
        startActivityForResult(context, intent, 2412, null)
    }

    fun writeTemplateToFile(file: OutputStream) {
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
        file.apply {
            write(Gson().toJson(template).toByteArray())
            close()
        }
    }

}