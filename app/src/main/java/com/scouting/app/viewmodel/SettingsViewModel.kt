package com.scouting.app.viewmodel

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.content.Context.MODE_PRIVATE
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.MatchManager
import java.io.File

class SettingsViewModel : ViewModel() {

    var defaultTemplateFileName = mutableStateOf("NONE")
    var defaultMatchOutputFileName = mutableStateOf(TextFieldValue("output-match.csv"))
    var defaultPitTemplateFileName = mutableStateOf("NONE")
    var defaultPitTemplateOutputFileName = mutableStateOf("output-pit.csv")

    var competitionScheduleFileName = mutableStateOf("none")
    var deviceAlliancePosition = mutableStateOf("RED")
    var deviceRobotPosition = mutableStateOf(0)
    var competitionMode = mutableStateOf(false)

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
        defaultTemplateFileName.value = File(
            preferences.getString("DEFAULT_TEMPLATE_FILE_NAME", "NONE")!!
        ).name
        competitionScheduleFileName.value =
            preferences.getString("COMPETITION_SCHEDULE_FILE_NAME", "NONE")!!
        // Need to add pit and match output file name and pit template file name
        competitionMode.value =
            preferences.getBoolean("COMPETITION_MODE", false)
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

    fun processTemplateFilePickerResult(filePath: String, context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val fileName = filePath.split("/").let { it[it.size - 1] }
         preferences.edit()
             .putString("DEFAULT_TEMPLATE_FILE_PATH_MATCH", filePath)
             .putString("DEFAULT_TEMPLATE_FILE_NAME_MATCH", fileName)
             .apply()
        defaultTemplateFileName.value = fileName
    }

    fun processScheduleFilePickerResult(filePath: String, context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val fileName = filePath.split("/").let { it[it.size - 1] }
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

    fun applyOutputFileNameChange(context: MainActivity) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putString(
                "DEFAULT_OUTPUT_FILE_NAME_MATCH",
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
                    .path.plus("/${processDefaultOutputFileName(defaultMatchOutputFileName.value.text)}")
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