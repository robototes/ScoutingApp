package com.scouting.app.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateFormatPit
import com.scouting.app.model.TemplateItem
import com.tencent.mmkv.MMKV
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

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

    var showingNoTemplateDialog = mutableStateOf(false)

    lateinit var scoutingScheduleManager: ScoutingScheduleManager
    private val preferences = MMKV.defaultMMKV()

    fun loadTemplateItems() {
        val templateType = if (scoutingType.value) "PIT" else "MATCH"
        val defaultTemplate =
            preferences.decodeString("DEFAULT_TEMPLATE_FILE_PATH_$templateType", "")
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

    fun populateMatchDataIfCompetition() {
        if (preferences.decodeBool("COMPETITION_MODE", false)) {
            currentAllianceMonitoring.value =
                preferences.decodeString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE"
            currentMatchMonitoring.value =
                TextFieldValue((scoutingScheduleManager.currentMatchScoutingIteration + 1).toString())
            currentTeamNumberMonitoring.value = TextFieldValue(scoutingScheduleManager.getCurrentTeam())
        } else {
            // Clear values from previous scouting session as the ViewModel persists
            currentMatchMonitoring.value = TextFieldValue()
            currentTeamNumberMonitoring.value = TextFieldValue()
        }
    }

    fun populatePitDataIfScheduled() {
        if (preferences.decodeBool("PIT_SCOUTING_MODE", false)) {
            val teamInfo = scoutingScheduleManager.getCurrentPitInfo()
            currentTeamNameMonitoring.value = TextFieldValue(teamInfo.second)
            currentTeamNumberMonitoring.value = TextFieldValue(teamInfo.first)
        } else {
            currentTeamNumberMonitoring.value = TextFieldValue()
            currentTeamNameMonitoring.value = TextFieldValue()
        }
    }

    fun resetMatchConfig() {
        currentMatchStage.value = 0
    }

    @Composable
    fun getCorrespondingMatchStageName(matchStage: Int): String {
        return when (matchStage) {
            0 -> stringResource(id = R.string.in_match_stage_autonomous_label)
            else -> stringResource(id = R.string.in_match_stage_teleoperated_label)
        }
    }

    fun saveScoutingDataToFile(context: MainActivity) {
        var csvRowDraft = ""
        val templateType = if (scoutingType.value) "PIT" else "MATCH"
        val specialColumnName = if (scoutingType.value) "name" else "match"
        val contentResolver = context.contentResolver
        val itemList = if (scoutingType.value) {
            pitListItems
        } else {
            autoListItems.toList() + teleListItems.toList()
        }

        // Add device name, scout name, match number and team number
        // OR if pit scouting add team name in place of match number
        val tabletName = preferences.decodeString("DEVICE_ALLIANCE_POSITION", "RED") + "-" +
                preferences.decodeInt("DEVICE_ROBOT_POSITION", 1).toString()
        val userSelectedOutputFileName = preferences.decodeString(
            "DEFAULT_OUTPUT_FILE_NAME_$templateType",
            "${FilePaths.DATA_DIRECTORY}/output-${templateType.toLowerCase(Locale.current)}.csv"
        )!!
        val csvHeaderRow = "device,scout,$specialColumnName,team," + saveKeyOrderList.value!!.joinToString(",")
        csvRowDraft += "$tabletName,${scoutName.value.text},${
            if (scoutingType.value) { currentTeamNameMonitoring.value.text } 
            else { currentMatchMonitoring.value.text }
        },${currentTeamNumberMonitoring.value.text},"

        // Append ordered, user-inputted match data
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

        var outputFile = File(userSelectedOutputFileName)

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        } else {
            // If the output file already exists, then check to see if the first row
            // of items (save keys) is the same as the order of save keys that we are
            // intending to save to. If the save keys are different then we create a new
            // file with the same user-selected name and appended UUID, so that it's not
            // confusing when reading the output file with a mess of data that isn't labeled
            // This will only come into effect when the user changes the template but doesn't
            // change the output file name 👍
            contentResolver.openInputStream(outputFile.toUri())?.use {
                if (it.reader().readLines()[0] != csvHeaderRow) {
                    outputFile = File("$userSelectedOutputFileName-${UUID.randomUUID()}")
                    outputFile.createNewFile()
                }
            }
        }

        if (outputFile.exists()) {
            lateinit var previousFileText: String
            // Check first line to determine whether there is a labeled save key row already there
            contentResolver.openInputStream(outputFile.toUri()).use {
                previousFileText = it!!.reader().readText()
                it.close()
            }
            FileOutputStream(outputFile).use { outputStream ->
                outputStream.bufferedWriter().use {
                    // If the file is newly created, add the save keys as the first row, then the
                    // data as the rest of the file's contents
                    it.write("${previousFileText.ifEmpty { csvHeaderRow }}\n$csvRowDraft")
                }
                outputStream.close()
            }
        }

        if (scoutingType.value) {
            if (preferences.decodeBool("PIT_SCOUTING_MODE", false)) {
                scoutingScheduleManager.moveToNextPit()
            }
        } else {
            if (preferences.decodeBool("COMPETITION_MODE", false)) {
                scoutingScheduleManager.moveToNextMatch()
            }
        }
    }

    private fun List<TemplateItem>.findItemValueWithKey(key: String): Any? {
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