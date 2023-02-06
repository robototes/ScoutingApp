package com.scouting.app.misc

import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.scouting.app.MainActivity
import java.io.File

class MatchManager {

    // Each row includes the match number and then a list of teams in
    // the order red 1, red 2, red 3, blue 1, blue 2, blue 3
    private val currentCompetitionScheduleCSV = mutableListOf<List<String>>()
    // The index inside of the list of teams participating in each match
    // 0 corresponds to red 1, an index of 2 would go to red 3, etc.
    private var monitoringTeamPositionIndex = 0

    var currentMatchNumber by mutableStateOf(0)

    fun loadCompetitionScheduleFromFile(filePath: String, context: MainActivity) {
        val scheduleFile = File(filePath)
        val preferences = context.getPreferences(MODE_PRIVATE)
        resetManager(context)
        currentCompetitionScheduleCSV.clear()
        context.contentResolver.openInputStream(scheduleFile.toUri())?.use {
            it.reader().readLines().forEach { item ->
                currentCompetitionScheduleCSV.add(item.split(","))
            }
        }
        preferences.edit().putString(
            "COMPETITION_SCHEDULE_CACHED",
            currentCompetitionScheduleCSV.joinToString("%")
        ).apply()
    }

    fun loadCachedCompetitionSchedule(context: MainActivity) {
        val preferences = context.getPreferences(MODE_PRIVATE)
        val cachedCompSchedule = preferences.getString("COMPETITION_SCHEDULE_CACHED", "")!!
        resetManager(context)
        currentCompetitionScheduleCSV.clear()
        if (cachedCompSchedule.isNotBlank()) {
            cachedCompSchedule.split("%").forEach {
                currentCompetitionScheduleCSV.add(it.removeSurrounding("[", "]").split(", "))
            }
        }
    }

    fun resetManager(context: MainActivity) {
        monitoringTeamPositionIndex = 0
        currentMatchNumber = 0
        context.getPreferences(MODE_PRIVATE).apply {
            if (getString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE") {
                monitoringTeamPositionIndex += 2
            }
            monitoringTeamPositionIndex += getInt("DEVICE_ROBOT_POSITION", 0)
        }
    }

    fun getCurrentTeam() : String =
        currentCompetitionScheduleCSV[currentMatchNumber][monitoringTeamPositionIndex]

}