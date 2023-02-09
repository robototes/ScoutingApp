package com.scouting.app.viewmodel

import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.MatchManager
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateItem
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateFormatPit
import java.io.File
import java.io.FileOutputStream

class ScoutingViewModel : ViewModel() {

    // true = pit, false = match
    var scoutingType = mutableStateOf(false)

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var pitListItems = mutableStateListOf<TemplateItem>()
    var saveKeyOrderList = mutableStateOf<List<String>?>(null)

    // true = blue, false = red
    var currentAllianceMonitoring = mutableStateOf(true)
    // 0 = auto, 1 = teleop
    var currentMatchStage = mutableStateOf(0)
    var currentTeamNumberMonitoring = mutableStateOf(TextFieldValue())
    var currentTeamNameMonitoring = mutableStateOf(TextFieldValue())
    var currentMatchMonitoring = mutableStateOf(TextFieldValue())
    var scoutName = mutableStateOf(TextFieldValue())

    lateinit var matchManager: MatchManager

    fun loadTemplateItems(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val templateType = if (scoutingType.value) "PIT" else "MATCH"
        val defaultTemplate = preferences.getString("DEFAULT_TEMPLATE_FILE_PATH_$templateType", "")
        if (defaultTemplate?.isNotBlank() == true) {
            File(defaultTemplate).bufferedReader().use { file ->
                val serializedTemplate = Gson().fromJson(
                    file.readText(),
                    if (scoutingType.value) {
                        TemplateFormatPit::class.java
                    } else {
                        TemplateFormatMatch::class.java
                    }
                )
                file.close()
                if (scoutingType.value) {
                    serializedTemplate as TemplateFormatPit
                    pitListItems.apply {
                        clear()
                        addAll(serializedTemplate.templateItems)
                    }
                    saveKeyOrderList.value = serializedTemplate.saveOrderByKey
                } else {
                    serializedTemplate as TemplateFormatMatch
                    autoListItems.apply {
                        clear()
                        addAll(serializedTemplate.autoTemplateItems)
                    }
                    teleListItems.apply {
                        clear()
                        addAll(serializedTemplate.teleTemplateItems)
                    }
                    saveKeyOrderList.value = serializedTemplate.saveOrderByKey
                }
            }
        }
    }

    fun populateMatchDataIfCompetition(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        if (preferences.getBoolean("COMPETITION_MODE", false)) {
            currentAllianceMonitoring.value =
                preferences.getString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE"
            currentMatchMonitoring.value = TextFieldValue((matchManager.currentMatchNumber + 1).toString())
            currentTeamNumberMonitoring.value = TextFieldValue(matchManager.getCurrentTeam())
        }
    }

    fun resetMatchConfig() {
        currentMatchStage.value = 0
    }

    @Composable
    fun getCorrespondingMatchStageName(matchStage: Int) : String {
        return when(matchStage) {
            0 -> stringResource(id = R.string.in_match_stage_autonomous_label)
            else -> stringResource(id = R.string.in_match_stage_teleoperated_label)
        }
    }

    fun saveMatchDataToFile(context: MainActivity) {
        var csvRowDraft = ""
        val itemList = if (scoutingType.value) {
            pitListItems
        } else {
            autoListItems.toList() + teleListItems.toList()
        }
        val preferences = context.getPreferences(MODE_PRIVATE)
        val templateType = if (scoutingType.value) "PIT" else "MATCH"
        val specialColumnName = if (scoutingType.value) "name" else "match"

        // Add device name, scout name, match number and team number
        // OR if pit scouting add team name in place of match number
        val tabletName = preferences.getString("DEVICE_ALLIANCE_POSITION", "RED") + "-"
                preferences.getInt("DEVICE_ROBOT_POSITION", 1)
        csvRowDraft += "$tabletName,${scoutName.value.text}," +
                "${
                    if (scoutingType.value) { 
                        currentTeamNameMonitoring.value.text 
                    } else { 
                        currentMatchMonitoring.value.text
                    }
                },${currentTeamNumberMonitoring.value.text},"

        // Append ordered user-inputted match data
        repeat(itemList.size) { index ->
            itemList.findItemValueWithKey(
                saveKeyOrderList.value?.get(index).toString()
            ).let { data ->
                csvRowDraft += data
                if (index < itemList.size - 1) {
                    csvRowDraft += ","
                }
            }
        }

        // Write all data to the specified output file name
        val outputFile = File(
            context.getPreferences(MODE_PRIVATE).getString(
                "DEFAULT_OUTPUT_FILE_NAME_$templateType",
                "output.csv"
            )!!
        )
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        if (outputFile.exists()) {
            lateinit var previousFileText: String
            context.contentResolver.openInputStream(outputFile.toUri()).use {
                previousFileText = it!!.reader().readText()
                it.close()
            }
            FileOutputStream(outputFile).use { outputStream ->
                outputStream.bufferedWriter().use {
                    // If the file is newly created, add the save keys as the first row
                    it.write("${
                        previousFileText.ifEmpty {
                            "device,scout,$specialColumnName,team," +
                                    saveKeyOrderList.value!!.joinToString(",")
                        }
                    }\n$csvRowDraft")
                }
                outputStream.close()
            }
        }
        if (preferences.getBoolean("COMPETITION_MODE", false)) {
            matchManager.currentMatchNumber++
        }
    }

    private fun List<TemplateItem>.findItemValueWithKey(key: String) : Any? {
        var foundItem: Any? = null
        forEachIndexed { _, item ->
            Log.e("DDD", item.type.name)
            when (key) {
                    item.saveKey -> {
                        foundItem = when (item.type) {
                            TemplateTypes.CHECK_BOX -> item.itemValueBoolean!!.value
                            TemplateTypes.TEXT_FIELD -> item.itemValueString!!.value
                            else /* SCORE_BAR, RATING_BAR OR TRI_SCORING */ -> item.itemValueInt!!.value
                        }
                        return@forEachIndexed
                    }
                    // We know that the only component that uses saveKey2 and 3 is
                    // the TRI_SCORING, which is always an integer value
                    item.saveKey2 -> {
                        foundItem = item.itemValue2Int!!.value
                        return@forEachIndexed
                    }
                    item.saveKey3 -> {
                        foundItem = item.itemValue3Int!!.value
                        return@forEachIndexed
                    }
                }
        }
        return foundItem
    }

}