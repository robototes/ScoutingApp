package com.scouting.app

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toFile
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.view.EditCSVOrderView
import com.scouting.app.view.FinishMatchView
import com.scouting.app.viewmodel.TemplateEditorViewModel
import com.scouting.app.view.HomePageView
import com.scouting.app.view.InMatchView
import com.scouting.app.view.SettingsView
import com.scouting.app.view.StartMatchView
import com.scouting.app.view.TemplateConfigView
import com.scouting.app.view.TemplateEditorView
import com.scouting.app.view.TemplateSaveView
import com.scouting.app.viewmodel.InMatchViewModel
import com.scouting.app.viewmodel.SettingsViewModel
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

object NavDestination {
    const val HomePage = "home"
    const val StartMatchConfig = "start-config"
    const val CreateTemplateConfig = "create-config"
    const val TemplateEditor = "template-editor"
    const val TemplateSave = "template-save"
    const val TemplatePreview = "template-preview"
    const val InMatch = "in-match"
    const val FinishMatch = "finish-match"
    const val RecordPitDataConfig = "pit-data-config"
    const val Settings = "settings"
    const val EditCSVOrder = "edit-csv"
}

class MainActivity : ComponentActivity() {

    private lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutingTheme {
                NavigationHost()
            }
        }
    }

    @Composable
    fun NavigationHost() {
        navigationController = rememberNavController()
        NavHost(
            navController = navigationController,
            startDestination = NavDestination.HomePage,
            modifier = Modifier.fillMaxSize(),
            builder = {
                composable(NavDestination.HomePage) {
                    HomePageView(navigationController)
                }
                composable(NavDestination.CreateTemplateConfig) {
                    TemplateConfigView(navigationController)
                }
                composable("${NavDestination.TemplateEditor}/{type}") {
                    TemplateEditorView(
                        navController = navigationController,
                        type = it.arguments?.getString("type", "match")!!
                    )
                }
                composable(NavDestination.TemplateSave) {
                    TemplateSaveView(navigationController)
                }
                composable(NavDestination.StartMatchConfig) {
                    StartMatchView(navigationController)
                }
                composable(NavDestination.InMatch) {
                    InMatchView(navigationController)
                }
                composable(NavDestination.Settings) {
                    SettingsView(navigationController)
                }
                composable(NavDestination.FinishMatch) {
                    FinishMatchView(navigationController)
                }
                composable(NavDestination.EditCSVOrder) {
                    EditCSVOrderView(navigationController)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                2412 -> {
                    resultData?.data?.let { contentResolver.openOutputStream(it) }?.let {
                        getViewModel(TemplateEditorViewModel::class.java)
                            .writeTemplateToFile(it)
                    }
                }

                24122 -> {
                    resultData?.data?.let {
                        getViewModel(SettingsViewModel::class.java)
                            .processFilePickerResult(
                                fileContent = BufferedReader(
                                    InputStreamReader(contentResolver.openInputStream(it))
                                ).readText(),
                                context = this
                            )
                    }
                }
            }
        }
    }

}