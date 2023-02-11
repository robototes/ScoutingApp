package com.scouting.app.misc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.scouting.app.MainActivity
import com.tencent.mmkv.MMKV
import java.io.File

class ScoutingScheduleManager {

    // Follows the format red 1, red 2, red 3, blue 1, blue 2, blue 3
    private val currentCompetitionScheduleCSV = mutableListOf<List<String>>()
    // Follows the format teamNumber, teamName
    private val currentPitScheduleCSV = mutableListOf<List<String>>()

    // The index inside of the list of teams participating in each match
    // 0 corresponds to red 1, an index of 2 would go to red 3, etc.
    private var monitoringTeamPositionIndex = 0

    var currentMatchScoutingIteration by mutableStateOf(0)
    var currentPitScoutingIteration by mutableStateOf(0)

    fun loadScheduleFromFile(filePath: String, context: MainActivity, matchSchedule: Boolean) {
        val scheduleFile = File(filePath)
        val preferences = MMKV.defaultMMKV()
        val currentListResource = if (matchSchedule) {
            currentCompetitionScheduleCSV
        } else {
            currentPitScheduleCSV
        }
        if (matchSchedule) { resetManagerMatch() }
        currentListResource.clear()
        context.contentResolver.openInputStream(scheduleFile.toUri())?.use {
            it.reader().readLines().forEach { item ->
                currentListResource.add(item.split(","))
            }
        }
        preferences.apply {
            val type = if (matchSchedule) "COMPETITION" else "PIT"
            encode("${type}_SCHEDULE_CACHED", currentListResource.joinToString("%"))
            encode("${type}_SCHEDULE_CURRENT_ITERATION", 0)
        }
    }

    fun loadCachedSchedule(matchSchedule: Boolean) {
        val preferences = MMKV.defaultMMKV()
        val type = if (matchSchedule) "COMPETITION" else "PIT"
        val currentListResource = if (matchSchedule) {
            currentCompetitionScheduleCSV
        } else {
            currentPitScheduleCSV
        }
        val cachedSchedule = preferences.decodeString("${type}_SCHEDULE_CACHED", "")!!
        if (matchSchedule) { resetManagerMatch() }
        currentListResource.clear()
        if (cachedSchedule.isNotBlank()) {
            cachedSchedule.split("%").forEach {
                currentListResource.add(it.removeSurrounding("[", "]").split(", "))
            }
            preferences.decodeInt("${type}_SCHEDULE_CURRENT_ITERATION", 0).let {
                if (matchSchedule) {
                    currentMatchScoutingIteration = it
                } else {
                    currentPitScoutingIteration = it
                }
            }
        }
    }

    fun resetManagerMatch() {
        monitoringTeamPositionIndex = 0
        currentMatchScoutingIteration = 0
        MMKV.defaultMMKV().apply {
            if (decodeString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE") {
                monitoringTeamPositionIndex += 2
            }
            monitoringTeamPositionIndex += decodeInt("DEVICE_ROBOT_POSITION", 0)
        }
    }

    fun getCurrentTeam(): String =
        currentCompetitionScheduleCSV[currentMatchScoutingIteration][monitoringTeamPositionIndex]

    // teamNumber, teamName
    fun getCurrentPitInfo() : Pair<String, String> {
        val item = currentPitScheduleCSV[currentPitScoutingIteration]
        return Pair(item[0], item[1])
    }

    fun getPitsLeftToScout() : Int = currentPitScheduleCSV.size -currentPitScoutingIteration

    fun moveToNextMatch() {
        currentMatchScoutingIteration++
        MMKV.defaultMMKV().encode("COMPETITION_SCHEDULE_CURRENT_ITERATION", currentMatchScoutingIteration)
    }

    fun moveToNextPit() {
        currentPitScoutingIteration++
        MMKV.defaultMMKV().encode("PIT_SCHEDULE_CURRENT_ITERATION", currentPitScoutingIteration)
    }

}