package com.scouting.app.viewmodel

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.misc.FilePaths
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateItem
import com.scouting.app.model.TemplateTypes
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.Writer

class InMatchViewModel : ViewModel() {

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()

    var saveKeyOrderList = mutableStateOf<List<String>?>(null)

    var currentTeamMonitoring = mutableStateOf(TextFieldValue())
    var currentMatchMonitoring = mutableStateOf(TextFieldValue())
    // true = blue, false = red
    var currentAllianceMonitoring = mutableStateOf(true)
    // 0 = auto, 1 = teleop, 2 = endgame
    var currentMatchStage = mutableStateOf(0)

    var scoutName = mutableStateOf(TextFieldValue())

    fun loadTemplateItems(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val defaultTemplate = preferences.getString("DEFAULT_TEMPLATE_FILE_PATH", "")
        scoutName.value = TextFieldValue(
            preferences.getString("CURRENT_SCOUT_NAME", "").toString()
        )
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

    fun saveMatchDataToFile(context: Context) {
        var csvRowDraft = ""
        val combinedItemList = autoListItems.plus(teleListItems)
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
        val outputFileChild = File(
            (context as MainActivity).getPreferences(MODE_PRIVATE).getString(
                "DEFAULT_OUTPUT_FILE_NAME",
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
                    it.write("${
                        previousFileText.ifEmpty {
                            saveKeyOrderList.value!!.joinToString(",")
                        }
                    }\n$csvRowDraft")
                }
                outputStream.close()
            }
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