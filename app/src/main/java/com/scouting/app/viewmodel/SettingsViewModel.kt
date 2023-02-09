package com.scouting.app.viewmodel

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.content.Context.MODE_PRIVATE
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.MatchManager
import org.json.JSONObject
import java.io.File

class SettingsViewModel : ViewModel() {

    var defaultMatchTemplateFileName = mutableStateOf("NONE")
    var defaultMatchOutputFileName = mutableStateOf(TextFieldValue("output-match.csv"))
    var defaultPitTemplateFileName = mutableStateOf("NONE")
    var defaultPitOutputFileName = mutableStateOf(TextFieldValue("output-pit.csv"))

    var competitionScheduleFileName = mutableStateOf("none")
    var deviceAlliancePosition = mutableStateOf("RED")
    var deviceRobotPosition = mutableStateOf(0)
    var competitionMode = mutableStateOf(false)

    // false = match, true = pit
    var fileNameEditingType = mutableStateOf(false)

    var showingFileNameDialog = mutableStateOf(false)
    var showingDevicePositionDialog = mutableStateOf(false)
    var showingCompetitionModeDialog = mutableStateOf(false)

    lateinit var matchManager: MatchManager

    fun loadSavedPreferences(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        deviceRobotPosition.value =
            preferences.getInt("DEVICE_ROBOT_POSITION", 1)
        deviceAlliancePosition.value =
            preferences.getString("DEVICE_ALLIANCE_POSITION", "RED")!!
        defaultMatchTemplateFileName.value = File(
            preferences.getString("DEFAULT_TEMPLATE_FILE_PATH_MATCH", "NONE")!!
        ).name
        defaultPitTemplateFileName.value = File(
            preferences.getString("DEFAULT_TEMPLATE_FILE_PATH_PIT", "NONE")!!
        ).name
        competitionScheduleFileName.value =
            preferences.getString("COMPETITION_SCHEDULE_FILE_NAME", "NONE")!!
        defaultMatchOutputFileName.value = TextFieldValue(
            File(
                preferences.getString("DEFAULT_OUTPUT_FILE_NAME_MATCH", "output-match.csv")!!
            ).name
        )
        defaultPitOutputFileName.value = TextFieldValue(
            File(
                preferences.getString("DEFAULT_OUTPUT_FILE_NAME_PIT", "output-pit.csv")!!
            ).name
        )
        competitionMode.value = preferences.getBoolean("COMPETITION_MODE", false)
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
            val preferences = context.getPreferences(MODE_PRIVATE)
            val fileName = File(filePath).name
            val prefKeyEnding = if (matchTemplate) "MATCH" else "PIT"
            preferences.edit()
                .putString("DEFAULT_TEMPLATE_FILE_PATH_$prefKeyEnding", filePath)
                .putString("DEFAULT_TEMPLATE_FILE_NAME_$prefKeyEnding", fileName)
                .apply()
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

    private fun checkIfTemplateIsMatch(filePath: String) : Boolean {
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

    fun processScheduleFilePickerResult(filePath: String, context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val fileName = File(filePath).name
        preferences.edit()
            .putString("COMPETITION_SCHEDULE_FILE_PATH", filePath)
            .putString("COMPETITION_SCHEDULE_FILE_NAME", fileName)
            .apply()
        competitionScheduleFileName.value = fileName
        matchManager.apply {
            loadCompetitionScheduleFromFile(filePath, context)
            resetManager(context)
        }
    }

    fun applyDevicePositionChange(context: MainActivity) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putString("DEVICE_ALLIANCE_POSITION", deviceAlliancePosition.value)
            .putInt("DEVICE_ROBOT_POSITION", deviceRobotPosition.value)
            .apply()
    }

    fun applyOutputFileNameChange(context: MainActivity, fileName: String) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putString(
                if (fileNameEditingType.value) {
                    "DEFAULT_OUTPUT_FILE_NAME_PIT"
                } else {
                    "DEFAULT_OUTPUT_FILE_NAME_MATCH"
                },
                FilePaths.DATA_DIRECTORY.plus("/${processDefaultOutputFileName(fileName)}")
            )
            .apply()
    }

    fun processDefaultOutputFileName(textToConvert: String) : String {
        return textToConvert.let {
            if (it.contains(".csv")) it else "$it.csv"
        }
    }

    fun setCompetitionMode(context: MainActivity, value: Boolean) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putBoolean("COMPETITION_MODE", value)
            .apply()
        matchManager.resetManager(context)
    }

}