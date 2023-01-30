package com.scouting.app.viewmodel

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.model.TemplateFormatMatch
import com.scouting.app.model.TemplateItem
import com.scouting.app.utilities.getPreferences
import org.json.JSONObject
import java.io.File

class InMatchViewModel : ViewModel() {

    var autoListItems = mutableStateListOf<TemplateItem>()
    var teleListItems = mutableStateListOf<TemplateItem>()
    var endgameListItems = mutableStateListOf<TemplateItem>()

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
                endgameListItems.apply {
                    clear()
                    addAll(serializedTemplate.endTemplateItems)
                }
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
            1 -> stringResource(id = R.string.in_match_stage_teleoperated_label)
            else -> stringResource(id = R.string.in_match_stage_endgame_label)
        }
    }

    fun saveMatchDataToFile() {

    }

}