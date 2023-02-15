package com.scouting.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.ScoutingType.MATCH
import com.scouting.app.misc.ScoutingType.PIT
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateFormatPit
import com.scouting.app.model.TemplateItem
import org.json.JSONObject
import java.io.File
import java.util.UUID

class TemplateEditorViewModel : ViewModel() {

    var currentTemplateType = MATCH
    var finalFileName by mutableStateOf(TextFieldValue())

    // Triple consists of saveKey, itemType and itemID
    var saveKeyList = mutableStateListOf<Triple<String, TemplateTypes, String>>()
    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var pitListItems = mutableStateListOf<TemplateItem>()

    var showingEditDialog by mutableStateOf(false)
    var currentListResource = pitListItems
    var currentEditItemIndex by mutableStateOf(0)

    fun importExistingTemplate(fileContents: String) {
        val isMatchTemplate = JSONObject(fileContents).getBoolean("isMatchTemplate")
        val deserializedTemplate = fileContents.let {
            Gson().fromJson(it, if (isMatchTemplate) TemplateFormatMatch::class.java else TemplateFormatPit::class.java)
        }
        if (isMatchTemplate) {
            deserializedTemplate as TemplateFormatMatch
            currentTemplateType = MATCH
            autoListItems.apply {
                clear()
                addAll(deserializedTemplate.autoTemplateItems)
            }
            teleListItems.apply {
                clear()
                addAll(deserializedTemplate.teleTemplateItems)
            }
        } else {
            deserializedTemplate as TemplateFormatPit
            currentTemplateType = PIT
            pitListItems.apply {
                clear()
                addAll(deserializedTemplate.templateItems)
            }
        }
    }

    /**
     * Create either a TemplateFormatMatch or Pit object holding all of the
     * data that will appear in the JSON template file, and then serialize
     * it and write to a JSON file.
     *
     * @param context - MainActivity context needed to use ContentResolver
     * to write to a file efficiently
     */
    fun writeTemplateToFile(context: MainActivity) {
        val outputFile = File(FilePaths.TEMPLATE_DIRECTORY, processFinalFileName())
        val template: Any = if (currentTemplateType == MATCH) {
            TemplateFormatMatch(
                title = processFinalFileName(),
                autoTemplateItems = autoListItems,
                teleTemplateItems = teleListItems,
                saveOrderByKey = createExportedSaveKeyList()
            )
        } else {
            TemplateFormatPit(
                title = processFinalFileName(),
                templateItems = pitListItems,
                saveOrderByKey = createExportedSaveKeyList()
            )
        }
        outputFile.createNewFile()
        context.contentResolver.openOutputStream(outputFile.toUri())?.use {
            it.write(Gson().toJson(template).toByteArray())
            it.close()
        }
        resetInstanceData()
    }

    /**
     * Create a list of Triple objects containing the save key, type and ID of
     * each component added to the corresponding template, to be edited in the
     * EditCSVOrderView
     */
    fun createSaveKeyList() {
        saveKeyList.clear()
        if (currentTemplateType == MATCH) {
            (autoListItems.toList() + teleListItems.toList())
        } else {
            pitListItems
        }.forEach {
            if (it.type != TemplateTypes.PLAIN_TEXT) {
                if (it.saveKey.isBlank()) {
                    it.saveKey = UUID.randomUUID().toString().substring(0, 8)
                }
                saveKeyList.add(Triple(it.saveKey, it.type, it.id))
                if (it.type == TemplateTypes.TRI_SCORING) {
                    if (it.saveKey2.toString().isBlank()) {
                        it.saveKey2 = UUID.randomUUID().toString().substring(0, 8)
                    }
                    if (it.saveKey3.toString().isBlank()) {
                        it.saveKey3 = UUID.randomUUID().toString().substring(0, 8)
                    }
                    saveKeyList.add(Triple(it.saveKey2.toString(), it.type, UUID.randomUUID().toString()))
                    saveKeyList.add(Triple(it.saveKey3.toString(), it.type, UUID.randomUUID().toString()))
                }
            }
        }
    }

    /**
     * After the user finishes editing the CSV save order, create a new list
     * of only the save keys in order that they wish it appears on the final
     * match or pit data output file
     */
    private fun createExportedSaveKeyList(): List<String> {
        val exportedSaveKeyList = mutableListOf<String>()
        saveKeyList.forEach { triple ->
            exportedSaveKeyList.add(triple.first)
        }
        return exportedSaveKeyList
    }

    /**
     * Make sure that the user added ".json" the their final file name, and
     * if not then add it so that it can be recognized when they select a
     * template in the settings menu
     */
    private fun processFinalFileName(): String {
        return finalFileName.text.let {
            if (it.contains(".json")) it else "$it.json"
        }
    }

    /**
     * Reset the current "instance" of the ViewModel after a user creates
     * a template so that if they navigate to the template editor again, it
     * won't still contain their old data
     */
    private fun resetInstanceData() {
        saveKeyList.clear()
        autoListItems.clear()
        teleListItems.clear()
        pitListItems.clear()
        finalFileName = TextFieldValue()
    }

}