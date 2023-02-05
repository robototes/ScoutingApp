package com.scouting.app.viewmodel

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.model.TemplateFormatMatch
import java.io.File
import java.io.FileOutputStream


class SettingsViewModel : ViewModel() {

    var defaultTemplateFileName = mutableStateOf("file.json")
    var defaultOutputFileName = mutableStateOf(TextFieldValue("output.csv"))
    var competitionScheduleFileName = mutableStateOf("none")
    var deviceAlliancePosition = mutableStateOf("RED")
    var deviceRobotPosition = mutableStateOf(0)

    var showingFileNameDialog = mutableStateOf(false)
    var showingDevicePositionDialog = mutableStateOf(false)

    fun requestFilePicker(context: MainActivity, code: Int, type: String) {
        ActivityCompat.startActivityForResult(
            context,
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                this.type = type
            },
            code,
            null
        )
    }

    fun processTemplateFilePickerResult(filePath: Uri, context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        Log.e("DD", filePath.toFile().path)
        // Save file path to SharedPreferences
         preferences.edit()
            .putString("DEFAULT_TEMPLATE_FILE_PATH", filePath.encodedPath)
            .apply()
            defaultTemplateFileName.value = filePath.lastPathSegment.toString()
            preferences.edit()
                .putString("DEFAULT_TEMPLATE_FILE_NAME", filePath.lastPathSegment)
                .apply()
    }

    fun processScheduleFilePickerResult() {

    }

    fun applyDevicePositionChange(context: MainActivity) {
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putString("DEVICE_ALLIANCE_POSITION", deviceAlliancePosition.value)
            .putInt("DEVICE_ROBOT_POSITION", deviceRobotPosition.value + 1)
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

}