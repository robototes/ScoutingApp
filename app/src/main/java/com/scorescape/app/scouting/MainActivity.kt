package com.scorescape.app.scouting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.scorescape.app.scouting.theme.ScorescapeTheme
import com.scorescape.app.scouting.utilities.getViewModel
import com.scorescape.app.scouting.view.HomePageView
import com.scorescape.app.scouting.view.TemplateConfigView
import com.scorescape.app.scouting.view.TemplateEditorView
import com.scorescape.app.scouting.view.TemplateSaveView
import com.scorescape.app.scouting.viewmodel.TemplateEditorViewModel
import java.io.File
import java.net.URI

object NavDestination {
    const val HomePage = "home"
    const val StartMatchConfig = "start-config"
    const val CreateTemplateConfig = "create-config"
    const val TemplateEditor = "template-editor"
    const val TemplateSave = "template-save"
    const val RecordPitDataConfig = "pit-data-config"
}

class MainActivity : ComponentActivity() {

    private lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScorescapeTheme {
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
                composable(route = NavDestination.HomePage) {
                    HomePageView(navController = navigationController)
                }
                composable(route = NavDestination.CreateTemplateConfig) {
                    TemplateConfigView(navController = navigationController)
                }
                composable(route = "${NavDestination.TemplateEditor}/{type}") {
                    TemplateEditorView(
                        navController = navigationController,
                        type = it.arguments?.getString("type", "match")!!
                    )
                }
                composable(route = NavDestination.TemplateSave) {
                    TemplateSaveView(navController = navigationController)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == 2412 && resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            resultData?.data?.let { contentResolver.openOutputStream(it) }?.let {
                navigationController.context.getViewModel(TemplateEditorViewModel::class.java)
                    .writeTemplateToFile(it)
            }
        }
    }

}