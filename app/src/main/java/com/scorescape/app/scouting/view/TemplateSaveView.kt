package com.scorescape.app.scouting.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scorescape.app.scouting.MainActivity
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.components.BasicInputField
import com.scorescape.app.scouting.components.LargeHeaderBar
import com.scorescape.app.scouting.components.MediumButton
import com.scorescape.app.scouting.components.SmallButton
import com.scorescape.app.scouting.theme.NeutralGrayDark
import com.scorescape.app.scouting.utilities.getViewModel
import com.scorescape.app.scouting.viewmodel.TemplateEditorViewModel

@Composable
fun TemplateSaveView(navController: NavController) {
    val viewModel = navController.context.getViewModel(TemplateEditorViewModel::class.java)
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
            text = String.format(
                stringResource(id = R.string.template_save_path_disclaimer_placeholder),
                viewModel.finalFileName.text
            ),
            color = NeutralGrayDark,
            modifier = Modifier.padding(start = 30.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 45.dp, top = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MediumButton(
                text = stringResource(id = R.string.template_save_button_preview_text),
                icon = painterResource(id = R.drawable.ic_view_eye),
                contentDescription = stringResource(id = R.string.ic_view_eye_content_desc),
                onClick = { /*TODO*/ },
                color = MaterialTheme.colors.secondary
            )
            MediumButton(
                text = stringResource(id = R.string.template_save_button_export_text),
                icon = painterResource(id = R.drawable.ic_document_export),
                contentDescription = stringResource(id = R.string.ic_document_export_content_desc),
                onClick = { viewModel.requestFilePicker(navController.context as MainActivity) },
                color = MaterialTheme.colors.primaryVariant
            )
        }
    }
}