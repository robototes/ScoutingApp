package com.scouting.app.viewmodel

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.scouting.app.MainActivity
import java.io.File

class SettingsViewModel : ViewModel() {

    var defaultTemplateFileName = mutableStateOf("file.json")

    fun requestFilePicker(context: MainActivity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        ActivityCompat.startActivityForResult(context, intent, 24122, null)
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
    }

}