package com.scouting.app.viewmodel

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.model.TemplateFormatMatch
import java.io.File
import java.io.FileOutputStream


class SettingsViewModel : ViewModel() {

    var defaultTemplateFileName = mutableStateOf("file.json")
    var defaultOutputFileName = mutableStateOf(TextFieldValue("output.csv"))

    var showingFileNameDialog = mutableStateOf(false)

    fun requestFilePicker(context: MainActivity, code: Int) {
        ActivityCompat.startActivityForResult(
            context,
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
            },
            code,
            null
        )
    }

    fun processFilePickerResult(fileContent: String, context: MainActivity) {
        val newFilePath = context.filesDir.path + "/DefaultTemplate.json"
        // Write the contents of the file to the app's data directory because
        // we cannot access the actual path of the selected file, however if
        // we create a new file in the private app data folder you don't need
        // any permissions and it will return the real path
        File(newFilePath).bufferedWriter().use {
            it.write(fileContent)
            it.close()
        }
        // Save file path to SharedPreferences
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putString("DEFAULT_TEMPLATE_FILE_PATH", newFilePath)
            .apply()
        Gson().fromJson(fileContent, TemplateFormatMatch::class.java).let {
            defaultTemplateFileName.value = "${it.gameName}-${it.gameYear}"
        }
    }

    fun applyOutputFileNameChange(context: MainActivity) {
        Log.e("DD", context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
            .path.plus(defaultOutputFileName.value.text))
        context.getPreferences(MODE_PRIVATE)
            .edit()
            .putString(
                "DEFAULT_OUTPUT_FILE_PATH",
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
                    .path.plus("/${defaultOutputFileName.value.text}")
            )
            .apply()
    }

}