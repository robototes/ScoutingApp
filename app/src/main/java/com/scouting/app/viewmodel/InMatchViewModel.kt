package com.scouting.app.viewmodel

import android.content.Context.MODE_PRIVATE
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
import com.scouting.app.model.TemplateTypes
import java.io.File
import java.io.FileOutputStream

class InMatchViewModel : ViewModel() {

    // true = pit, false = match
    var scoutingType = mutableStateOf(false)

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var saveKeyOrderList = mutableStateOf<List<String>?>(null)

    // true = blue, false = red
    var currentAllianceMonitoring = mutableStateOf(true)
    // 0 = auto, 1 = teleop
    var currentMatchStage = mutableStateOf(0)
    var currentTeamMonitoring = mutableStateOf(TextFieldValue())
    var currentMatchMonitoring = mutableStateOf(TextFieldValue())
    var scoutName = mutableStateOf(TextFieldValue())

    lateinit var matchManager: MatchManager

    fun loadTemplateItems(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val defaultTemplate = preferences.getString("DEFAULT_TEMPLATE_FILE_PATH_MATCH", "")
        if (defaultTemplate?.isNotBlank() == true) {
            File(defaultTemplate).bufferedReader().use { file ->
                val serializedTemplate = Gson().fromJson(file.readText(), TemplateFormatMatch::class.java)
                file.close()
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

    fun populateMatchDataIfCompetition(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        if (preferences.getBoolean("COMPETITION_MODE", false)) {
            currentAllianceMonitoring.value =
                preferences.getString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE"
            currentMatchMonitoring.value = TextFieldValue((matchManager.currentMatchNumber + 1).toString())
            currentTeamMonitoring.value = TextFieldValue(matchManager.getCurrentTeam())
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
        val combinedItemList = (autoListItems + teleListItems)
        val preferences = context.getPreferences(MODE_PRIVATE)

        // Add device name, scout name, match number and team number
        val tabletName = preferences.getString("DEVICE_ALLIANCE_POSITION", "RED") +
                preferences.getInt("DEVICE_ROBOT_POSITION", 1)
        csvRowDraft += "$tabletName,${scoutName.value.text},${currentMatchMonitoring.value.text},${currentTeamMonitoring.value.text},"

        // Append ordered user-inputted match data
        repeat(combinedItemList.size) { index ->
            combinedItemList.findItemValueWithKey(
                saveKeyOrderList.value?.get(index).toString()
            ).let { data ->
                csvRowDraft += data
                if (index < combinedItemList.size - 1) {
                    csvRowDraft += ","
                }
            }
        }

        // Write all data to the specified output file name
        val outputFileChild = File(
            context.getPreferences(MODE_PRIVATE).getString(
                "DEFAULT_OUTPUT_FILE_NAME_MATCH",
                "output.csv"
            )!!
        )
        val outputFile = File("${FilePaths.DATA_DIRECTORY}/$outputFileChild")
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
                            "device,scout,match,team," + saveKeyOrderList.value!!.joinToString(",")
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