package com.scouting.app

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
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
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.scouting.app.misc.FilePaths
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.RequestCode.COMPETITION_SCHEDULE_FILE_PICK
import com.scouting.app.misc.RequestCode.MATCH_TEMPLATE_FILE_PICK
import com.scouting.app.misc.RequestCode.PIT_SCOUTING_SCHEDULE_FILE_PICK
import com.scouting.app.misc.RequestCode.PIT_TEMPLATE_FILE_PICK
import com.scouting.app.misc.RequestCode.TEMPLATE_EDITOR_IMAGE_FILE_PICK
import com.scouting.app.misc.RequestCode.TEMPLATE_EDITOR_IMPORT_FILE_PICK
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.view.HomePageView
import com.scouting.app.view.scouting.FinishScoutingView
import com.scouting.app.view.scouting.ScoutingView
import com.scouting.app.view.scouting.StartMatchView
import com.scouting.app.view.scouting.StartPitScoutingView
import com.scouting.app.view.settings.SettingsView
import com.scouting.app.view.template.EditCSVOrderView
import com.scouting.app.view.template.TemplateEditorView
import com.scouting.app.view.template.TemplateSaveView
import com.tencent.mmkv.MMKV
import java.io.File


class MainActivity : ComponentActivity() {

    private lateinit var navigationController: NavHostController
    lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        configureStorage()
        appContainer = AppContainer()
        appContainer.scheduleManager.apply {
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
        val scheduleManager = appContainer.scheduleManager
        val homePageViewModel = appContainer.homePageViewModel
        val scoutingViewModel = appContainer.scoutingViewModel
        val settingsViewModel = appContainer.settingsViewModel
        val templateEditorViewModel = appContainer.templateEditorViewModel
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
                    HomePageView(navigationController, scheduleManager, homePageViewModel, settingsViewModel)
                }
                composable(
                    route = "${NavDestination.TemplateEditor}/{type}",
                    arguments = listOf(navArgument("type") { type = NavType.StringType })
                ) {
                    TemplateEditorView(
                        navController = navigationController,
                        viewModel = templateEditorViewModel,
                        type = it.arguments?.getString("type", "match")!!
                    )
                }
                composable(NavDestination.TemplateEditor) {
                    TemplateEditorView(navigationController, templateEditorViewModel)
                }
                composable(NavDestination.EditCSVOrder) {
                    EditCSVOrderView(navigationController, templateEditorViewModel)
                }
                composable(NavDestination.TemplateSave) {
                    TemplateSaveView(navigationController, templateEditorViewModel, settingsViewModel)
                }
                composable(NavDestination.StartMatchScouting) {
                    StartMatchView(navigationController, scheduleManager, scoutingViewModel)
                }
                composable(NavDestination.StartPitScouting) {
                    StartPitScoutingView(navigationController, scheduleManager, scoutingViewModel)
                }
                composable(
                    route = "${NavDestination.Scouting}/{type}",
                    arguments = listOf(navArgument("type") { type = NavType.BoolType })
                ) {
                    ScoutingView(
                        navController = navigationController,
                        viewModel = scoutingViewModel,
                        scoutingMatch = it.arguments?.getBoolean("type", true) ?: false
                    )
                }
                composable(NavDestination.Settings) {
                    SettingsView(navigationController, scheduleManager, settingsViewModel)
                }
                composable(NavDestination.FinishScouting) {
                    FinishScoutingView(navigationController, scoutingViewModel)
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

    /**
     * Use the UnicornFilePicker library to open a file picker, allowing the
     * user to choose a competition schedule, match template, etc.
     *
     * @param code - The code that will be sent with the Intent, to be later
     * decoded in onActivityResult (MainActivity) that is used to determine what
     * to do with the returned data
     * @param type - The file type to be shown in the file picker. Some examples:
     * "csv", "json", or "apk"
     */
    fun requestFilePicker(code: Int, vararg type: String) {
        UnicornFilePicker.from(this)
            .addConfigBuilder()
            .selectMultipleFiles(false)
            .setRootDirectory(Environment.getExternalStorageDirectory().absolutePath)
            .showHiddenFiles(false)
            .addItemDivider(true)
            .setFilters(type)
            .theme(R.style.FilePickerTheme)
            .build()
            .forResult(code)
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            resultData?.let { data ->
                val settingsViewModel = appContainer.settingsViewModel
                val templateEditorViewModel = appContainer.templateEditorViewModel
                when (requestCode) {
                    MATCH_TEMPLATE_FILE_PICK, PIT_TEMPLATE_FILE_PICK -> {
                        settingsViewModel.processTemplateFilePickerResult(
                            filePath = data.getStringArrayListExtra("filePaths")!![0],
                            context = this@MainActivity,
                            matchTemplate = requestCode == MATCH_TEMPLATE_FILE_PICK
                        )
                    }

                    COMPETITION_SCHEDULE_FILE_PICK, PIT_SCOUTING_SCHEDULE_FILE_PICK -> {
                        settingsViewModel.processScheduleFilePickerResult(
                            filePath = data.getStringArrayListExtra("filePaths")!![0],
                            context = this@MainActivity,
                            matchSchedule = requestCode == COMPETITION_SCHEDULE_FILE_PICK
                        )
                    }

                    TEMPLATE_EDITOR_IMPORT_FILE_PICK -> {
                        templateEditorViewModel.importExistingTemplate(
                            fileContents = contentResolver.openInputStream(
                                File(data.getStringArrayListExtra("filePaths")!![0]).toUri()
                            )!!.use {
                                val contents = it.reader().readText()
                                it.close()
                                return@use contents
                            }
                        )
                        navigationController.navigate(NavDestination.TemplateEditor)
                    }

                    TEMPLATE_EDITOR_IMAGE_FILE_PICK -> {
                        templateEditorViewModel.importImageToCurrentIndex(
                            filePath = File(data.getStringArrayListExtra("filePaths")!![0]).toUri()
                        )
                    }
                }
            }
        }
    }
}