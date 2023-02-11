package com.scouting.app.misc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.scouting.app.MainActivity
import com.tencent.mmkv.MMKV
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
        val preferences = MMKV.defaultMMKV()
        resetManager()
        currentCompetitionScheduleCSV.clear()
        context.contentResolver.openInputStream(scheduleFile.toUri())?.use {
            it.reader().readLines().forEach { item ->
                currentCompetitionScheduleCSV.add(item.split(","))
            }
        }
        preferences.apply {
            encode(
                "COMPETITION_SCHEDULE_CACHED",
                currentCompetitionScheduleCSV.joinToString("%")
            )
            encode("COMPETITION_MODE_CURRENT_MATCH", 0)
        }
    }

    fun loadCachedCompetitionSchedule() {
        val preferences = MMKV.defaultMMKV()
        val cachedCompSchedule = preferences.decodeString("COMPETITION_SCHEDULE_CACHED", "")!!
        resetManager()
        currentCompetitionScheduleCSV.clear()
        if (cachedCompSchedule.isNotBlank()) {
            cachedCompSchedule.split("%").forEach {
                currentCompetitionScheduleCSV.add(it.removeSurrounding("[", "]").split(", "))
            }
            currentMatchNumber = preferences.decodeInt("COMPETITION_MODE_CURRENT_MATCH", 0)
        }
    }

    fun resetManager() {
        monitoringTeamPositionIndex = 0
        currentMatchNumber = 0
        MMKV.defaultMMKV().apply {
            if (decodeString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE") {
                monitoringTeamPositionIndex += 2
            }
            monitoringTeamPositionIndex += decodeInt("DEVICE_ROBOT_POSITION", 0)
        }
    }

    fun getCurrentTeam(): String =
        currentCompetitionScheduleCSV[currentMatchNumber][monitoringTeamPositionIndex]

    fun moveToNextMatch() {
        currentMatchNumber++
        MMKV.defaultMMKV().encode("COMPETITION_MODE_CURRENT_MATCH", currentMatchNumber)
    }

}