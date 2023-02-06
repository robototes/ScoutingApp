package com.scouting.app

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.MatchManager
import com.scouting.app.misc.NavDestination
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.view.EditCSVOrderView
import com.scouting.app.view.FinishMatchView
import com.scouting.app.view.HomePageView
import com.scouting.app.view.InMatchView
import com.scouting.app.view.SettingsView
import com.scouting.app.view.StartMatchView
import com.scouting.app.view.TemplateEditorView
import com.scouting.app.view.TemplateSaveView
import com.scouting.app.viewmodel.SettingsViewModel
import java.io.File


class MainActivity : ComponentActivity() {

    private lateinit var navigationController: NavHostController
    private val matchManager = MatchManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureStorage()
        matchManager.loadCachedCompetitionSchedule(this)
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
                    HomePageView(navigationController, matchManager)
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
                    StartMatchView(navigationController, matchManager)
                }
                composable(NavDestination.InMatch) {
                    InMatchView(navigationController)
                }
                composable(NavDestination.Settings) {
                    SettingsView(navigationController, matchManager)
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

    /**
     * Request storage permission so that we can save files,
     * if the user denies this permission it will repeatedly
     * ask them every time they open the app until they grant it ☺
     */
    private fun configureStorage() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_request_denial_toast_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                File(FilePaths.TEMPLATE_DIRECTORY).mkdirs()
                File(FilePaths.DATA_DIRECTORY).mkdirs()
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            resultData?.let { data ->
                getViewModel(SettingsViewModel::class.java).apply {
                    when (requestCode) {
                        2412 -> {
                            processTemplateFilePickerResult(
                                filePath = data.getStringArrayListExtra("filePaths")!![0],
                                context = this@MainActivity
                            )
                        }

                        2413 -> {
                            processScheduleFilePickerResult(
                                filePath = data.getStringArrayListExtra("filePaths")!![0],
                                context = this@MainActivity
                            )
                        }
                    }
                }
            }
        }
    }
}