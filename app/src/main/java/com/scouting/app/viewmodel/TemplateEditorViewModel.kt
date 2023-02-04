package com.scouting.app.viewmodel

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
import com.scouting.app.MainActivity
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateFormatPit
import com.scouting.app.model.TemplateItem
import com.scouting.app.model.TemplateTypes
import java.io.OutputStream
import java.util.UUID

class TemplateEditorViewModel : ViewModel() {

    var currentTemplateType = "match"

    var gameNameTextValue by mutableStateOf(TextFieldValue())
    var gameYearTextValue by mutableStateOf(TextFieldValue())
    var finalFileName by mutableStateOf(
        TextFieldValue("${gameNameTextValue.text}${gameYearTextValue.text}.json")
    )

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var pitListItems = mutableStateListOf<TemplateItem>()

    // Triple consists of saveKey, itemType and itemID
    var saveKeyList = mutableStateListOf<Triple<String, TemplateTypes, String>>()

    var showingEditDialog by mutableStateOf(false)

    var currentListResource by mutableStateOf(pitListItems)
    var currentSelectedTab = mutableStateOf(0)
    var currentEditItemIndex by mutableStateOf(0)

    fun createSaveKeyList() {
        saveKeyList.clear()
        val listResources = listOf(autoListItems, teleListItems)
        listResources.forEach { list ->
            list.forEach {
                saveKeyList.add(Triple(it.saveKey, it.type, it.id))
                if (it.type == TemplateTypes.TRI_SCORING) {
                    saveKeyList.add(Triple(it.saveKey2.toString(), it.type, UUID.randomUUID().toString()))
                    saveKeyList.add(Triple(it.saveKey3.toString(), it.type, UUID.randomUUID().toString()))
                }
            }
        }
    }

    fun createExportedSaveKeyList() : List<String> {
        val exportedSaveKeyList = mutableListOf<String>()
        saveKeyList.forEach { triple ->
            exportedSaveKeyList.add(triple.first)
        }
        return exportedSaveKeyList
    }

    /**
     * Request the system to open the file picker so that the user
     * can choose where the template is saved.
     */
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

    /**
     * Serialize all the layout data that the user edited and write
     * it to the file at the location selected by the user
     * in JSON format (which will later be deserialized in the match view)
     */
    fun writeTemplateToFile(file: OutputStream) {
        val template: Any = if (currentTemplateType == "match") {
            TemplateFormatMatch(
                title = finalFileName.text,
                autoTemplateItems = autoListItems,
                teleTemplateItems = teleListItems,
                saveOrderByKey = createExportedSaveKeyList()
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