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


class SettingsViewModel : ViewModel() {

    var defaultTemplateFileName = mutableStateOf("file.json")
    var defaultOutputFileName = mutableStateOf(TextFieldValue("output.csv"))
    var competitionScheduleFileName = mutableStateOf("none")
    var deviceAlliancePosition = mutableStateOf("RED")
    var deviceRobotPosition = mutableStateOf(0)
    var competitionMode = mutableStateOf(false)

    var showingFileNameDialog = mutableStateOf(false)
    var showingDevicePositionDialog = mutableStateOf(false)

    lateinit var matchManager: MatchManager

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
             .putString("DEFAULT_TEMPLATE_FILE_PATH", filePath)
             .putString("DEFAULT_TEMPLATE_FILE_NAME", fileName)
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
                "DEFAULT_OUTPUT_FILE_PATH",
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
                    .path.plus("/${processDefaultOutputFileName()}")
            )
            .apply()
    }

    fun processDefaultOutputFileName() : String {
        return defaultOutputFileName.value.text.let {
            if (it.contains(".csv")) it else "$it.csv"
        }
    }

    fun beginCompetitionMode(context: MainActivity) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putBoolean("COMPETITION_MODE", true)
            .apply()
        matchManager.resetManager(context)
    }

    fun endCompetitionMode(context: MainActivity) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putBoolean("COMPETITION_MODE", false)
            .apply()
    }

}