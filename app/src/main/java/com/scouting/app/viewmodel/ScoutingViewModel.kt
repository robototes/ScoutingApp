package com.scouting.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.misc.AllianceType
import com.scouting.app.misc.AllianceType.RED
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.MatchStage.AUTO
import com.scouting.app.misc.MatchStage.TELEOP
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.ScoutingType.MATCH
import com.scouting.app.misc.ScoutingType.PIT
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateFormatPit
import com.scouting.app.model.TemplateItem
import com.scouting.app.utilities.quoteForCSV
import com.tencent.mmkv.MMKV
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ScoutingViewModel : ViewModel() {

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var pitListItems = mutableStateListOf<TemplateItem>()
    var saveKeyOrderList by mutableStateOf<List<String>?>(null)

    var scoutingType by mutableStateOf(PIT)
    var currentMatchStage by mutableStateOf(AUTO)
    var currentAllianceMonitoring by mutableStateOf(RED)
    var currentTeamNumberMonitoring by mutableStateOf(TextFieldValue())
    var currentTeamNameMonitoring by mutableStateOf(TextFieldValue())
    var currentMatchMonitoring by mutableStateOf(TextFieldValue())
    var scoutName by mutableStateOf(TextFieldValue())

    var showingNoTemplateDialog by mutableStateOf(false)

    lateinit var scoutingScheduleManager: ScoutingScheduleManager
    private val preferences = MMKV.defaultMMKV()

    /**
     * Deserialize template items from user-selected and created JSON template
     * to list variables stored in the ViewModel, to later be interpreted and
     * laid out accordingly by the view
     *
     * @return True if the loading failed (e.g., no template file), false if successful
     */
    fun loadTemplateItems(): Boolean {
        val templateType = if (scoutingType == PIT) "PIT" else "MATCH"
        val defaultTemplate = preferences.decodeString("DEFAULT_TEMPLATE_FILE_PATH_$templateType", "") ?: return true
        val templateFile = File(defaultTemplate)
        if (defaultTemplate.isBlank() || !templateFile.exists()) {
            return true
        }
        val fileText = templateFile.readText()
        val serializedTemplate = Gson().fromJson(
            fileText,
            if (scoutingType == PIT) {
                TemplateFormatPit::class.java
            } else {
                TemplateFormatMatch::class.java
            }
        )
        if (scoutingType == PIT) {
            serializedTemplate as TemplateFormatPit
            pitListItems.apply {
                clear()
                addAll(serializedTemplate.templateItems)
            }
            saveKeyOrderList = serializedTemplate.saveOrderByKey
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
            saveKeyOrderList = serializedTemplate.saveOrderByKey
        }
        return false
    }

    /**
     * Update the fields in StartMatchView. If a competition schedule is
     * enabled, match number and team number are set according to the set
     * device position, otherwise they are set to blank. Alliance color is
     * always set.
     */
    fun populateMatchData() {
        currentAllianceMonitoring =
            AllianceType.valueOf(preferences.decodeString("DEVICE_ALLIANCE_POSITION", "RED")!!)
        if (preferences.decodeBool("COMPETITION_MODE", false)) {
            currentMatchMonitoring =
                TextFieldValue((scoutingScheduleManager.currentMatchScoutingIteration + 1).toString())
            currentTeamNumberMonitoring = TextFieldValue(scoutingScheduleManager.getCurrentTeam())
        } else {
            currentMatchMonitoring = TextFieldValue()
            currentTeamNumberMonitoring = TextFieldValue()
        }
    }

    /**
     * Reset text fields in the StartPitScoutingView with the team name and the
     * team number according to the pit scouting schedule, or as blank if pit
     * scouting mode is disabled.
     */
    fun populatePitDataIfScheduled() {
        if (preferences.decodeBool("PIT_SCOUTING_MODE", false)) {
            val teamInfo = scoutingScheduleManager.getCurrentPitInfo()
            currentTeamNameMonitoring = TextFieldValue(teamInfo.second)
            currentTeamNumberMonitoring = TextFieldValue(teamInfo.first)
        } else {
            currentTeamNumberMonitoring = TextFieldValue()
            currentTeamNameMonitoring = TextFieldValue()
        }
    }

    /**
     * Append to or write to a new file the match data inputted by the user,
     * to a CSV file named by the user in the settings menu. The items in the
     * file conform to the save key order created by the user when making the
     * template.
     */
    fun saveScoutingDataToFile(context: MainActivity) {
        val csvRowDraft = StringBuilder()
        val templateType = if (scoutingType == PIT) "PIT" else "MATCH"
        val contentResolver = context.contentResolver
        val itemList = if (scoutingType == PIT) {
            pitListItems
        } else {
            autoListItems.toList() + teleListItems.toList()
        }

        val tabletName = preferences.decodeString("DEVICE_ALLIANCE_POSITION", "RED") + "-" +
                preferences.decodeInt("DEVICE_ROBOT_POSITION", 1).toString()
        val matchType = preferences.decodeString("MATCH_TYPE", "NONE")
        val csvHeaderRow = if (scoutingType == PIT) {
            "device,scout,name,team," + saveKeyOrderList!!.joinToString(",")
        } else {
            "device,scout,match,match-type,team," + saveKeyOrderList!!.joinToString(",")
        }

        csvRowDraft.append(
            if (scoutingType == PIT) {
                "$tabletName,${scoutName.text},${currentTeamNameMonitoring.text},${currentTeamNumberMonitoring.text},"
            } else {
                "$tabletName,${scoutName.text},${currentMatchMonitoring.text},$matchType,${currentTeamNumberMonitoring.text},"
            }
        )


        // Append ordered, user-inputted match data
        csvRowDraft.append(saveKeyOrderList!!.joinToString(",") { key ->
            itemList.findItemValueWithKey(key).toString().quoteForCSV()
        })

        val userSelectedOutputFileName = preferences.decodeString(
            "DEFAULT_OUTPUT_FILE_NAME_$templateType",
            "${FilePaths.DATA_DIRECTORY}/output-${templateType.toLowerCase(Locale.current)}.csv"
        )!!
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
                    outputFile = File("${userSelectedOutputFileName.removeSuffix(".csv")}-${UUID.randomUUID()}.csv")
                    outputFile.createNewFile()
                }
            }
        }

        if (outputFile.exists()) {
            var outputIsEmpty: Boolean
            contentResolver.openInputStream(outputFile.toUri()).use {
                outputIsEmpty = it!!.reader().readText().isEmpty()
                it.close()
            }
            FileOutputStream(outputFile, true).use { outputStream ->
                outputStream.bufferedWriter().use {
                    if (outputIsEmpty) {
                        it.appendLine(csvHeaderRow)
                    }
                    it.appendLine(csvRowDraft)
                }
                outputStream.close()
            }
        }

        if (scoutingType == PIT) {
            if (preferences.decodeBool("PIT_SCOUTING_MODE", false)) {
                scoutingScheduleManager.moveToNextPit()
            }
        } else {
            if (preferences.decodeBool("COMPETITION_MODE", false)) {
                scoutingScheduleManager.moveToNextMatch()
            }
        }
    }

    /**
     * Fetch the corresponding item value using the save key
     */
    private fun List<TemplateItem>.findItemValueWithKey(key: String): Any? {
        var foundItem: Any? = null
        forEachIndexed { index, item ->
            try {
                when (key) {
                    item.saveKey -> {
                        foundItem = when (item.type) {
                            TemplateTypes.CHECK_BOX -> item.itemValueBoolean!!.value
                            TemplateTypes.TEXT_FIELD -> item.itemValueString!!.value
                            else /* SCORE_BAR, RATING_BAR, TRI_BUTTON OR TRI_SCORING */ -> item.itemValueInt!!.value
                        }
                        return@forEachIndexed
                    }
                    // We know that the only component that uses saveKey2 and saveKey3
                    // is the TRI_SCORING component, which is always an integer value
                    item.saveKey2 -> {
                        foundItem = item.itemValue2Int!!.value
                        return@forEachIndexed
                    }

                    item.saveKey3 -> {
                        foundItem = item.itemValue3Int!!.value
                        return@forEachIndexed
                    }
                }
            } catch (e: java.lang.NullPointerException) {
                Log.e("ScoutingApp", "NPE saving item $item at index $index! Using null", e)
                foundItem = null
                return@forEachIndexed
            }
        }
        return foundItem
    }

    /**
     * If the user is in competition mode and they edit the text in the match number field,
     * then update the team number in the competition schedule only if it is a valid match
     * number
     *
     * @param newValue - The new match number intended to be set
     */
    fun updateTeamNumberAccordingToMatch(newValue: String) {
        scoutingScheduleManager.apply {
            if (newValue.toInt() < currentCompetitionScheduleCSV.size) {
                jumpToMatch(newValue.toInt())
                currentTeamNumberMonitoring = TextFieldValue(getCurrentTeam())
            }
        }
    }

    /**
     * Reset all values in the current match stage to their default values
     */
    fun clearCurrentData() {
        val listToDealWith = if (scoutingType == MATCH) {
            if (currentMatchStage == TELEOP) {
                teleListItems
            } else {
                autoListItems
            }
        } else {
            pitListItems
        }
        listToDealWith.forEach { item ->
            item.apply {
                itemValueBoolean = mutableStateOf(false)
                itemValueString = mutableStateOf("")
                itemValueInt = mutableStateOf(0)
                itemValue2Int = mutableStateOf(0)
                itemValue3Int = mutableStateOf(0)
            }
        }
    }

}