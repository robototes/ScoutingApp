package com.scouting.app.view.template

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scouting.app.R
import com.scouting.app.components.DottedRoundBox
import com.scouting.app.components.LargeHeaderBar
import com.scouting.app.components.SmallButton
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.theme.NeutralGrayMedium
import com.scouting.app.theme.ScoutingTheme
import com.scouting.app.utilities.longPressEffect
import com.scouting.app.viewmodel.TemplateEditorViewModel
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.move
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun EditCSVOrderView(navController: NavController, viewModel: TemplateEditorViewModel) {
    val listReorderState = rememberReorderState()
    ScoutingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                LargeHeaderBar(
                    title = stringResource(id = R.string.template_edit_csv_header_title),
                    navController = navController,
                    endContent = {
                        SmallButton(
                            text = stringResource(id = R.string.template_edit_csv_save_header_move_on_button),
                            icon = painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = stringResource(id = R.string.ic_arrow_forward_content_desc),
                            onClick = {
                                navController.navigate(NavDestination.TemplateSave)
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            outlineStyle = true
                        )
                    }
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .reorderable(
                            state = listReorderState,
                            onMove = { old, new ->
                                viewModel.saveKeyList.move(old.index, new.index)
                            }
                        ),
                    state = listReorderState.listState
                ) {
                    itemsIndexed(
                        items = viewModel.saveKeyList,
                        key = { _, item ->
                            item.third
                        }
                    ) { _, item ->
                        var height by remember { mutableStateOf(0.dp) }
                        var setHeight by remember { mutableStateOf(false) }
                        DottedRoundBox(
                            modifier = Modifier
                                .padding(horizontal = 30.dp, vertical = 8.dp)
                                .detectReorderAfterLongPress(listReorderState)
                                .longPressEffect(listReorderState.offsetByKey(item.third))
                                .onGloballyPositioned { layoutCoordinates ->
                                    if (!setHeight) {
                                        setHeight = true
                                        height =
                                            layoutCoordinates.size.height.let { it - (it * 0.25) }.dp
                                    }
                                },
                            height = height
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_drag_indicator),
                                    contentDescription = stringResource(id = R.string.ic_drag_indicator_content_desc)
                                )
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = NeutralGrayMedium),
                                    modifier = Modifier.padding(15.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(15.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.template_edit_csv_save_key_prefix),
                                            style = MaterialTheme.typography.bodyLarge + TextStyle(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                        Text(
                                            text = item.first,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = NeutralGrayMedium),
                                    modifier = Modifier.padding(
                                        end = 15.dp,
                                        top = 15.dp,
                                        bottom = 15.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(15.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.template_edit_csv_save_type_prefix),
                                            style = MaterialTheme.typography.bodyLarge + TextStyle(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                        Text(
                                            text = when (item.second) {
                                                TemplateTypes.TEXT_FIELD -> stringResource(id = R.string.template_edit_csv_type_string)
                                                TemplateTypes.TRI_SCORING,
                                                TemplateTypes.SCORE_BAR,
                                                TemplateTypes.RATING_BAR,
                                                TemplateTypes.TRI_BUTTON -> stringResource(id = R.string.template_edit_csv_type_int)
                                                TemplateTypes.QUAD_BUTTON -> stringResource(id = R.string.template_edit_csv_type_int)

                                                else -> stringResource(id = R.string.template_edit_csv_type_boolean)
                                            },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = NeutralGrayMedium)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(15.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.template_edit_csv_item_type_prefix),
                                            style = MaterialTheme.typography.bodyLarge + TextStyle(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                        Text(
                                            text = when (item.second) {
                                                TemplateTypes.TEXT_FIELD -> stringResource(id = R.string.template_edit_csv_item_text_field_prefix)
                                                TemplateTypes.TRI_SCORING -> stringResource(id = R.string.template_edit_csv_item_tri_scoring_prefix)
                                                TemplateTypes.CHECK_BOX -> stringResource(id = R.string.template_edit_csv_item_checkbox_prefix)
                                                TemplateTypes.TRI_BUTTON -> stringResource(id = R.string.template_edit_csv_item_tri_button_prefix)
                                                TemplateTypes.QUAD_BUTTON -> stringResource(id = R.strring.template_edit_csv_item_quad_button_prefix)
                                                TemplateTypes.RATING_BAR -> stringResource(id = R.string.template_edit_csv_item_rating_bar_prefix)
                                                else -> stringResource(id = R.string.template_edit_csv_item_counter_prefix)
                                            },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
