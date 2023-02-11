package com.scouting.app

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.ScoutingScheduleManager
import com.scouting.app.misc.NavDestination
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.view.HomePageView
import com.scouting.app.view.scouting.FinishScoutingView
import com.scouting.app.view.scouting.ScoutingView
import com.scouting.app.view.scouting.StartMatchView
import com.scouting.app.view.scouting.StartPitScoutingView
import com.scouting.app.view.settings.SettingsView
import com.scouting.app.view.template.EditCSVOrderView
import com.scouting.app.view.template.TemplateEditorView
import com.scouting.app.view.template.TemplateSaveView
import com.scouting.app.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV
import java.io.File


class MainActivity : ComponentActivity() {

    private lateinit var navigationController: NavHostController
    private val scoutingScheduleManager = ScoutingScheduleManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        configureStorage()
        scoutingScheduleManager.apply {
            loadCachedSchedule(true)
            loadCachedSchedule(false)
        }
        setContent {
            ScoutingTheme {
                NavigationHost()
            }
        }
    }

    @Composable
    @OptIn(ExperimentalAnimationApi::class)
    fun NavigationHost() {
        navigationController = rememberAnimatedNavController()
        AnimatedNavHost(
            navController = navigationController,
            startDestination = NavDestination.HomePage,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Up)
            },
            exitTransition = {
                fadeOut()
            },
            popExitTransition = {
                slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Down)
            },
            builder = {
                composable(NavDestination.HomePage) {
                    HomePageView(navigationController, scoutingScheduleManager)
                }
                composable(
                    route = "${NavDestination.TemplateEditor}/{type}",
                    arguments = listOf(navArgument("type") { type = NavType.StringType })
                ) {
                    TemplateEditorView(
                        navController = navigationController,
                        type = it.arguments?.getString("type", "match")!!
                    )
                }
                composable(NavDestination.EditCSVOrder) {
                    EditCSVOrderView(navigationController)
                }
                composable(NavDestination.TemplateSave) {
                    TemplateSaveView(navigationController)
                }
                composable(NavDestination.StartMatchScouting) {
                    StartMatchView(navigationController, scoutingScheduleManager)
                }
                composable(NavDestination.StartPitScouting) {
                    StartPitScoutingView(navigationController, scoutingScheduleManager)
                }
                composable(
                    route = "${NavDestination.Scouting}/{type}",
                    arguments = listOf(navArgument("type") { type = NavType.BoolType })
                ) {
                    ScoutingView(
                        navController = navigationController,
                        scoutingMatch = it.arguments?.getBoolean("type", true) ?: false
                    )
                }
                composable(NavDestination.Settings) {
                    SettingsView(navigationController, scoutingScheduleManager)
                }
                composable(NavDestination.FinishScouting) {
                    FinishScoutingView(navigationController)
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
                            processSettingsFilePickerResult(
                                filePath = data.getStringArrayListExtra("filePaths")!![0],
                                context = this@MainActivity,
                                matchTemplate = true
                            )
                        }

                        2414 -> {
                            processSettingsFilePickerResult(
                                filePath = data.getStringArrayListExtra("filePaths")!![0],
                                context = this@MainActivity,
                                matchTemplate = false
                            )
                        }

                        2413, 2415 -> {
                            processScheduleFilePickerResult(
                                filePath = data.getStringArrayListExtra("filePaths")!![0],
                                context = this@MainActivity,
                                matchSchedule = requestCode == 2413
                            )
                        }
                    }
                }
            }
        }
    }
}