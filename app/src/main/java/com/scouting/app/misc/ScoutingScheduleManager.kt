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
    val currentCompetitionScheduleCSV = mutableListOf<List<String>>()
    // Follows the format teamNumber, teamName
    private val currentPitScheduleCSV = mutableListOf<List<String>>()

    // The index inside of the list of teams participating in each match
    // 0 corresponds to red/blue 1, an index of 2 would go to red/blue 3, etc.
    private var monitoringTeamPositionIndex = 0

    var currentMatchScoutingIteration by mutableStateOf(0)
    var currentPitScoutingIteration by mutableStateOf(0)

    /**
     * Read a CSV file containing a pit or match scouting schedule, and add
     * it to the lists in the ScoutingScheduleManager then save the entire CSV
     * to MMKV and set the current iteration to 0
     *
     * @param filePath - The file path pointing to the schedule that is being loaded
     * @param context - MainActivity context so that we can read the files using
     * a ContentResolver to open an input stream
     * @param matchSchedule - Whether the file being loaded is a match or pit schedule
     */
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

    /**
     * Called when the app is first opened, if there is an existing match or pit
     * schedule and the corresponding scouting mode is turned on in settings, load
     * that schedule into the ScoutingScheduleManager so that we can pick up where
     * we left off when the app was last closed
     *
     * @param matchSchedule - Whether to load a cached pit or match schedule
     */
    fun loadCachedSchedule(matchSchedule: Boolean) {
        val preferences = MMKV.defaultMMKV()
        val type = if (matchSchedule) "COMPETITION" else "PIT"
        val currentListResource = if (matchSchedule) {
            currentCompetitionScheduleCSV
        } else {
            currentPitScheduleCSV
        }
        val cachedSchedule = preferences.decodeString("${type}_SCHEDULE_CACHED", "")!!
        if (cachedSchedule.isNotBlank()) {
            if (matchSchedule) { resetManagerMatch() }
            currentListResource.clear()
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

    /**
     * Reset the ScoutingScheduleManager match data, starting at match 1 again
     * and setting the monitoringTeamPositionIndex based on the set device position
     */
    fun resetManagerMatch() {
        monitoringTeamPositionIndex = 0
        currentMatchScoutingIteration = 0
        MMKV.defaultMMKV().apply {
            monitoringTeamPositionIndex =
                if (decodeString("DEVICE_ALLIANCE_POSITION", "RED") == "BLUE") 2 else -1
            monitoringTeamPositionIndex += decodeInt("DEVICE_ROBOT_POSITION", 0)
        }
    }

    /**
     * In match scouting mode, get the team corresponding to the user's device position
     * and the current match number
     */
    fun getCurrentTeam(): String =
        currentCompetitionScheduleCSV[currentMatchScoutingIteration][monitoringTeamPositionIndex]

    /**
     * Fetch information about the current pit being scouted
     *
     * @return Triple holding the team number as first and team name as second
     */
    fun getCurrentPitInfo() : Pair<String, String> {
        val item = currentPitScheduleCSV[currentPitScoutingIteration]
        return Pair(item[0], item[1])
    }

    /**
     * Return how many pits the device has left to scout based on the schedule
     */
    fun getPitsLeftToScout() : Int = currentPitScheduleCSV.size - currentPitScoutingIteration

    /**
     * Increment the match number counter and save this with MMKV
     */
    fun moveToNextMatch() {
        if (currentMatchScoutingIteration < currentCompetitionScheduleCSV.size - 1) {
            currentMatchScoutingIteration++
            MMKV.defaultMMKV().encode("COMPETITION_SCHEDULE_CURRENT_ITERATION", currentMatchScoutingIteration)
        } else {
            resetManagerMatch()
            MMKV.defaultMMKV().encode("COMPETITION_MODE", false)
        }
    }

    /**
     * Move to the specified match as the current one, useful if the user
     * leaves and needs to jump a few matches forward or in some cases go back
     *
     * @param number - A non-zero based match number (as the user would input it)
     */
    fun jumpToMatch(number: Int) {
        currentMatchScoutingIteration = number - 1
    }

    /**
     * Increment the current row of the pit scouting schedule we're
     * on and save this to MMKV
     */
    fun moveToNextPit() {
        if (currentPitScoutingIteration < currentPitScheduleCSV.size - 1) {
            currentPitScoutingIteration++
            MMKV.defaultMMKV().encode("PIT_SCHEDULE_CURRENT_ITERATION", currentPitScoutingIteration)
        } else {
            currentPitScoutingIteration = 0
            MMKV.defaultMMKV().encode("PIT_SCOUTING_MODE", false)
        }
    }

}