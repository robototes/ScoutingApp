package com.scouting.app

import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.viewmodel.HomePageViewModel
import com.scouting.app.viewmodel.ScoutingViewModel
import com.scouting.app.viewmodel.SettingsViewModel
import com.scouting.app.viewmodel.TemplateEditorViewModel

class AppContainer {
    val scheduleManager = ScoutingScheduleManager()
    val homePageViewModel = HomePageViewModel()
    val scoutingViewModel = ScoutingViewModel()
    val settingsViewModel = SettingsViewModel()
    val templateEditorViewModel = TemplateEditorViewModel()
}