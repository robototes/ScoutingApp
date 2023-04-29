package com.scouting.app.viewmodel

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.AllianceType
import com.scouting.app.misc.AllianceType.RED
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.ScoutingType.MATCH
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.io.File

class SettingsViewModel : ViewModel() {

    var defaultMatchTemplateFileName by mutableStateOf("NONE")
    var defaultMatchOutputFileName by mutableStateOf(TextFieldValue("output-match.csv"))
    var defaultPitTemplateFileName by mutableStateOf("NONE")
    var defaultPitOutputFileName by mutableStateOf(TextFieldValue("output-pit.csv"))

    var competitionScheduleFileName by mutableStateOf("NONE")
    var pitScheduleFileName by mutableStateOf("NONE")
    var deviceAlliancePosition by mutableStateOf(RED)
    var deviceRobotPosition by mutableStateOf(0)
    var competitionMode by mutableStateOf(false)
    var pitScoutingMode by mutableStateOf(false)

    var fileNameEditingType by mutableStateOf(MATCH)
    var scheduledScoutingModeType by mutableStateOf(MATCH)

    var showingFileNameDialog by mutableStateOf(false)
    var showingDevicePositionDialog by mutableStateOf(false)
    var showingScheduledScoutingModeDialog by mutableStateOf(false)

    lateinit var scoutingScheduleManager: ScoutingScheduleManager
    private val preferences = MMKV.defaultMMKV()

    /**
     * Set all switches, input fields etc. to the values they were last
     * saved as in MMKV
     */
    fun loadSavedPreferences() {
        preferences.apply {
            deviceRobotPosition = decodeInt("DEVICE_ROBOT_POSITION", 1)
            deviceAlliancePosition = AllianceType.valueOf(decodeString("DEVICE_ALLIANCE_POSITION", "RED")!!)
            defaultMatchTemplateFileName = File(
                decodeString("DEFAULT_TEMPLATE_FILE_PATH_MATCH", "NONE")!!
            ).name
            defaultPitTemplateFileName = File(
                decodeString("DEFAULT_TEMPLATE_FILE_PATH_PIT", "NONE")!!
            ).name
            competitionScheduleFileName = decodeString("COMPETITION_SCHEDULE_FILE_NAME", "NONE")!!
            pitScheduleFileName = decodeString("PIT_SCHEDULE_FILE_NAME", "NONE")!!
            defaultMatchOutputFileName = TextFieldValue(
                File(decodeString("DEFAULT_OUTPUT_FILE_NAME_MATCH", "output-match.csv")!!).name
            )
            defaultPitOutputFileName = TextFieldValue(
                File(decodeString("DEFAULT_OUTPUT_FILE_NAME_PIT", "output-pit.csv")!!).name
            )
            competitionMode = decodeBool("COMPETITION_MODE", false)
            pitScoutingMode = decodeBool("PIT_SCOUTING_MODE", false)
        }
    }

    /**
     * Receive the data from the file picker when selecting a template, and save
     * the file name and path to MMKV. This function also checks whether the template
     * is of the correct type using checkIfTemplateIsMatch() and will display an error
     * to the user if for example, they choose a pit template and the intention was
     * to select a match template
     *
     * @param filePath - The path directing to the template selected by the user
     * @param context - MainActivity context needed to show a Toast message in case
     * an error needs to be shown to the user
     * @param matchTemplate - Whether the intention of the file picker was to select
     * a match template
     */
    fun processTemplateFilePickerResult(filePath: String, context: MainActivity, matchTemplate: Boolean) {
        if (checkIfTemplateIsMatch(filePath) == matchTemplate) {
            val fileName = File(filePath).name
            val prefKeyEnding = if (matchTemplate) "MATCH" else "PIT"
            preferences.apply {
                encode("DEFAULT_TEMPLATE_FILE_PATH_$prefKeyEnding", filePath)
                encode("DEFAULT_TEMPLATE_FILE_NAME_$prefKeyEnding", fileName)
            }
            if (matchTemplate) {
                defaultMatchTemplateFileName = fileName
            } else {
                defaultPitTemplateFileName = fileName
            }
        } else {
            Toast.makeText(
                context,
                context.resources.getString(R.string.settings_pick_incorrect_template_toast_text),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Read the "isMatchTemplate" field of a JSON template and determine
     * whether it is a match template or not
     *
     * @param filePath - The path of the file to check whether it is a match
     * template or not
     * @return - a Boolean value telling whether the template is or isn't
     * a match template
     */
    private fun checkIfTemplateIsMatch(filePath: String): Boolean {
        return File(filePath).bufferedReader().use {
            // Read as JSONObject instead of serializing because match and
            // pit templates are different and would cause a crash if fed into
            // Gson, but since we know they both have the isMatchTemplate
            // field then we can individually check it without messing with the rest
            val matchTemplate = JSONObject(it.readText()).getBoolean("isMatchTemplate")
            it.close()
            matchTemplate
        }
    }

    /**
     * Receive the data from the file picker when selecting a match or pit
     * scouting schedule, then save the path and name to preferences and
     * configure the ScoutingScheduleManager to load the schedule.
     *
     * One good feature to add here would be to verify whether the template
     * is a match or pit schedule, however I'm not sure how you would do that
     * (maybe check the length of each row) in the CSV?
     *
     * @param filePath - The path pointing to the selected schedule
     * @param context - MainActivity context used to get a ContentResolver when
     * reading the schedule file in the ScoutingScheduleManager
     * @param matchSchedule - Whether the intention of the file picker was to
     * select a match schedule or not
     */
    fun processScheduleFilePickerResult(filePath: String, context: MainActivity, matchSchedule: Boolean) {
        val fileName = File(filePath).name
        val typePrefix = if (matchSchedule) "COMPETITION" else "PIT"
        preferences.apply {
            encode("${typePrefix}_SCHEDULE_FILE_PATH", filePath)
            encode("${typePrefix}_SCHEDULE_FILE_NAME", fileName)
        }
        if (matchSchedule) {
            competitionScheduleFileName = fileName
        } else {
            pitScheduleFileName = fileName
        }
        scoutingScheduleManager.apply {
            loadScheduleFromFile(filePath, context, matchSchedule)
            if (matchSchedule) {
                resetManagerMatch()
            }
        }
    }

    /**
     * Save the new device position to MMKV (RED/BLUE and position (1,2 or 3)
     */
    fun applyDevicePositionChange() {
        preferences.apply {
            encode("DEVICE_ALLIANCE_POSITION", deviceAlliancePosition.name)
            encode("DEVICE_ROBOT_POSITION", deviceRobotPosition)
        }
    }

    /**
     * Format and save the user-selected file name for either the match
     * or pit output data. All output data will be saved to the directory
     * Documents/scouting/data
     */
    fun applyOutputFileNameChange(fileName: String) {
        preferences.encode(
            "DEFAULT_OUTPUT_FILE_NAME_${fileNameEditingType.name}",
            FilePaths.DATA_DIRECTORY.plus("/${processDefaultOutputFileName(fileName)}")
        )
    }

    /**
     * Make sure that when a user enters a new file name that it ends with
     * ".csv", otherwise add it before saving
     */
    fun processDefaultOutputFileName(textToConvert: String): String {
        return textToConvert.let {
            if (it.contains(".csv")) it else "$it.csv"
        }
    }

    /**
     * Save whether competition mode is enabled or not and reset the
     * ScoutingScheduleManager either way
     */
    fun changeCompetitionMode(value: Boolean) {
        preferences.encode("COMPETITION_MODE", value)
        scoutingScheduleManager.resetManagerMatch()
    }

    /**
     * Save whether pit scouting mode is on or not
     */
    fun changePitScoutingMode(value: Boolean) {
        preferences.encode("PIT_SCOUTING_MODE", value)
    }

}