package com.scouting.app.view.template

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.MainActivity
import com.scouting.app.R
import com.scouting.app.components.BasicInputField
import com.scouting.app.components.LargeHeaderBar
import com.scouting.app.components.MediumButton
import com.scouting.app.misc.NavDestination
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.getViewModel
import com.scouting.app.utilities.returnTo
import com.scouting.app.viewmodel.TemplateEditorViewModel

@Composable
fun TemplateSaveView(navController: NavController) {
    val viewModel = navController.context.getViewModel(TemplateEditorViewModel::class.java)
    var setAsDefaultTemplate by remember { mutableStateOf(false) }
    ScoutingTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                LargeHeaderBar(
                    title = stringResource(id = R.string.template_save_header_title),
                    navController = navController
                )
                BasicInputField(
                    icon = painterResource(id = R.drawable.ic_edit_pen),
                    contentDescription = stringResource(id = R.string.ic_edit_pen_content_desc),
                    hint = stringResource(id = R.string.template_save_file_name_input_hint),
                    textFieldValue = viewModel.finalFileName,
                    onValueChange = {
                        viewModel.finalFileName = it
                    },
                    modifier = Modifier
                        .padding(start = 30.dp, end = 40.dp, top = 35.dp, bottom = 20.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.template_save_path_disclaimer_placeholder),
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5F),
                    modifier = Modifier.padding(start = 30.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 45.dp, top = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.template_save_set_default_template_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Switch(
                        checked = setAsDefaultTemplate,
                        onCheckedChange = { value -> setAsDefaultTemplate = value },
                        colors = SwitchDefaults.colors(
                            uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(
                                0.2F
                            )
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 45.dp, top = 40.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    MediumButton(
                        text = stringResource(id = R.string.template_save_button_export_text),
                        icon = painterResource(id = R.drawable.ic_document_export),
                        contentDescription = stringResource(id = R.string.ic_document_export_content_desc),
                        onClick = {
                            navController.apply {
                                if (viewModel.finalFileName.text.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.template_edit_save_fail_toast),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    viewModel.writeTemplateToFile(context as MainActivity, setAsDefaultTemplate)
                                    returnTo(NavDestination.HomePage)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.template_edit_save_success_toast),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}