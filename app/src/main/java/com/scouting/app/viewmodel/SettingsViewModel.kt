package com.scouting.app.viewmodel

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.ScoutingScheduleManager
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.io.File

class SettingsViewModel : ViewModel() {

    var defaultMatchTemplateFileName = mutableStateOf("NONE")
    var defaultMatchOutputFileName = mutableStateOf(TextFieldValue("output-match.csv"))
    var defaultPitTemplateFileName = mutableStateOf("NONE")
    var defaultPitOutputFileName = mutableStateOf(TextFieldValue("output-pit.csv"))

    var competitionScheduleFileName = mutableStateOf("NONE")
    var pitScheduleFileName = mutableStateOf("NONE")
    var deviceAlliancePosition = mutableStateOf("RED")
    var deviceRobotPosition = mutableStateOf(0)
    var competitionMode = mutableStateOf(false)
    var pitScoutingMode = mutableStateOf(false)

    // false = match, true = pit
    var fileNameEditingType = mutableStateOf(false)

    var showingFileNameDialog = mutableStateOf(false)
    var showingDevicePositionDialog = mutableStateOf(false)
    var showingScheduledScoutingModeDialog = mutableStateOf(false)
    // true = match, false = pit
    var scheduledScoutingModeType = mutableStateOf(true)

    lateinit var scoutingScheduleManager: ScoutingScheduleManager
    private val preferences = MMKV.defaultMMKV()

    fun loadSavedPreferences() {
        preferences.apply {
            deviceRobotPosition.value = decodeInt("DEVICE_ROBOT_POSITION", 1)
            deviceAlliancePosition.value = decodeString("DEVICE_ALLIANCE_POSITION", "RED")!!
            defaultMatchTemplateFileName.value = File(
                decodeString("DEFAULT_TEMPLATE_FILE_PATH_MATCH", "NONE")!!
            ).name
            defaultPitTemplateFileName.value = File(
                decodeString("DEFAULT_TEMPLATE_FILE_PATH_PIT", "NONE")!!
            ).name
            competitionScheduleFileName.value =
                decodeString("COMPETITION_SCHEDULE_FILE_NAME", "NONE")!!
            pitScheduleFileName.value =
                decodeString("PIT_SCHEDULE_FILE_NAME", "NONE")!!
            defaultMatchOutputFileName.value = TextFieldValue(
                File(
                    decodeString("DEFAULT_OUTPUT_FILE_NAME_MATCH", "output-match.csv")!!
                ).name
            )
            defaultPitOutputFileName.value = TextFieldValue(
                File(
                    decodeString("DEFAULT_OUTPUT_FILE_NAME_PIT", "output-pit.csv")!!
                ).name
            )
            competitionMode.value = decodeBool("COMPETITION_MODE", false)
            pitScoutingMode.value = decodeBool("PIT_SCOUTING_MODE", false)
        }
    }

    fun requestFilePicker(context: MainActivity, code: Int, type: String) {
        UnicornFilePicker.from(context)
            .addConfigBuilder()
            .selectMultipleFiles(false)
            .setRootDirectory(Environment.getExternalStorageDirectory().absolutePath)
            .showHiddenFiles(false)
            .addItemDivider(true)
            .setFilters(arrayOf(type))
            .theme(R.style.FilePickerTheme)
            .build()
            .forResult(code)
    }

    fun processSettingsFilePickerResult(
        filePath: String,
        context: MainActivity,
        matchTemplate: Boolean
    ) {
        if (checkIfTemplateIsMatch(filePath) == matchTemplate) {
            val fileName = File(filePath).name
            val prefKeyEnding = if (matchTemplate) "MATCH" else "PIT"
            preferences.apply {
                encode("DEFAULT_TEMPLATE_FILE_PATH_$prefKeyEnding", filePath)
                encode("DEFAULT_TEMPLATE_FILE_NAME_$prefKeyEnding", fileName)
            }
            if (matchTemplate) {
                defaultMatchTemplateFileName.value = fileName
            } else {
                defaultPitTemplateFileName.value = fileName
            }
        } else {
            Toast.makeText(
                context,
                context.resources.getString(R.string.settings_pick_incorrect_template_toast_text),
                Toast.LENGTH_LONG
            ).show()
        }
    }

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

    fun processScheduleFilePickerResult(filePath: String, context: MainActivity, matchSchedule: Boolean) {
        val fileName = File(filePath).name
        val typePrefix = if (matchSchedule) "COMPETITION" else "PIT"
        preferences.apply {
            encode("${typePrefix}_SCHEDULE_FILE_PATH", filePath)
            encode("${typePrefix}_SCHEDULE_FILE_NAME", fileName)
        }
        (if (matchSchedule) competitionScheduleFileName else pitScheduleFileName).value = fileName
        scoutingScheduleManager.apply {
            loadScheduleFromFile(filePath, context, matchSchedule)
            if (matchSchedule) { resetManagerMatch() }
        }
    }

    fun applyDevicePositionChange() {
        preferences.apply {
            encode("DEVICE_ALLIANCE_POSITION", deviceAlliancePosition.value)
            encode("DEVICE_ROBOT_POSITION", deviceRobotPosition.value)
        }
    }

    fun applyOutputFileNameChange(fileName: String) {
        preferences.encode(
            if (fileNameEditingType.value) {
                "DEFAULT_OUTPUT_FILE_NAME_PIT"
            } else {
                "DEFAULT_OUTPUT_FILE_NAME_MATCH"
            },
            FilePaths.DATA_DIRECTORY.plus("/${processDefaultOutputFileName(fileName)}")
        )
    }

    fun processDefaultOutputFileName(textToConvert: String): String {
        return textToConvert.let {
            if (it.contains(".csv")) it else "$it.csv"
        }
    }

    fun setCompetitionMode(value: Boolean) {
        preferences.encode("COMPETITION_MODE", value)
        scoutingScheduleManager.resetManagerMatch()
    }

    fun setPitScoutingMode(value: Boolean) {
        preferences.encode("PIT_SCOUTING_MODE", value)
    }

}