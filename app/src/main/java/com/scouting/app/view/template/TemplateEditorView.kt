package com.scouting.app.view.template

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.scouting.app.R
import com.scouting.app.components.*
import com.scouting.app.misc.NavDestination
import com.scouting.app.misc.ScoutingType
import com.scouting.app.misc.TemplateTypes
import com.scouting.app.model.TemplateItem
import com.scouting.app.utilities.longPressEffect
import com.scouting.app.viewmodel.TemplateEditorViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.move
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable

@Composable
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
fun TemplateEditorView(navController: NavController, viewModel: TemplateEditorViewModel, type: String? = null) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val pagerState = rememberPagerState(initialPage = 0)
    type?.let { viewModel.currentTemplateType = ScoutingType.valueOf(it) }
    Surface {
        ModalBottomSheetLayout(
            sheetContent = {
                TemplateListSheet(viewModel, bottomSheetState)
            },
            sheetState = bottomSheetState,
            sheetShape = MaterialTheme.shapes.large
        ) {
            Column {
                TemplateEditorHeader(
                    type = viewModel.currentTemplateType,
                    viewModel = viewModel,
                    navController = navController,
                    pagerState = pagerState
                )
                if (viewModel.currentTemplateType == ScoutingType.MATCH) {
                    viewModel.apply {
                        currentListResource = when (pagerState.currentPage) {
                            0 -> autoListItems
                            else -> teleListItems
                        }
                    }
                    HorizontalPager(
                        count = 2,
                        state = pagerState
                    ) { page ->
                        when (page) {
                            0 -> TemplateEditorList(
                                viewModel,
                                bottomSheetState,
                                viewModel.autoListItems
                            )

                            1 -> TemplateEditorList(
                                viewModel,
                                bottomSheetState,
                                viewModel.teleListItems
                            )
                        }
                    }
                } else {
                    viewModel.apply {
                        currentListResource = pitListItems
                    }
                    TemplateEditorList(
                        viewModel,
                        bottomSheetState,
                        viewModel.pitListItems
                    )
                }
            }
            EditTemplateDialog(
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TemplateEditorHeader(
    type: ScoutingType,
    viewModel: TemplateEditorViewModel,
    navController: NavController,
    pagerState: PagerState
) {
    val async = rememberCoroutineScope()
    MediumHeaderBar(
        title = stringResource(id = R.string.template_editor_header_title),
        navController = navController,
        iconLeft = painterResource(id = R.drawable.ic_save_icon),
        contentDescription = stringResource(id = R.string.ic_save_icon_content_desc),
        onIconLeftClick = {
            viewModel.createSaveKeyList()
            navController.navigate(NavDestination.EditCSVOrder)
        }
    )
    if (type != ScoutingType.PIT) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TabLayout(
                items = listOf(
                    stringResource(id = R.string.template_editor_auto_header),
                    stringResource(id = R.string.template_editor_tele_header)
                ),
                selection = remember {
                    derivedStateOf { pagerState.currentPage }
                },
                onSelectionChange = {
                    async.launch { pagerState.animateScrollToPage(it) }
                },
                modifier = Modifier.padding(top = 10.dp),
                size = 2
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun TemplateEditorList(
    viewModel: TemplateEditorViewModel,
    sheetState: ModalBottomSheetState,
    listResource: SnapshotStateList<TemplateItem>
) {
    val coroutineScope = rememberCoroutineScope()
    val listReorderState = rememberReorderState()
    AnimatedVisibility(visible = true) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp)
                .reorderable(
                    state = listReorderState,
                    onMove = { old, new ->
                        listResource.move(old.index, new.index)
                    }
                ),
            state = listReorderState.listState
        ) {
            itemsIndexed(items = listResource, key = { _, item -> item.id }) { index, item ->
                var height by remember { mutableStateOf(0.dp) }
                var layoutCalculated by remember { mutableStateOf(false) }
                AnimatedVisibility(visible = !viewModel.showingEditDialog) {
                    DottedRoundBox(
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 8.dp)
                            .detectReorderAfterLongPress(listReorderState)
                            .longPressEffect(listReorderState.offsetByKey(item.id))
                            .onGloballyPositioned { layoutCoordinates ->
                                if (!layoutCalculated) {
                                    layoutCalculated = true
                                    height =
                                        layoutCoordinates.size.height.let { it - (it * 0.25) }.dp
                                }
                            }
                            .clickable {
                                viewModel.apply {
                                    currentEditItemIndex = index
                                    showingEditDialog = true
                                }
                            },
                        height = height
                    ) {
                        Row(
                            horizontalArrangement = if (item.type == TemplateTypes.PLAIN_TEXT) {
                                Arrangement.Start
                            } else {
                                Arrangement.SpaceBetween
                            },
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_drag_indicator),
                                contentDescription = stringResource(id = R.string.ic_drag_indicator_content_desc)
                            )
                            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                                ListItemFromType(item)
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SmallButton(
                        text = stringResource(id = R.string.template_editor_add_item),
                        icon = painterResource(id = R.drawable.ic_plus_item),
                        contentDescription = stringResource(id = R.string.ic_plus_item_content_desc),
                        onClick = {
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        },
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 40.dp, top = 25.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ListItemFromType(item: TemplateItem) {
    when (item.type) {
        TemplateTypes.SCORE_BAR -> {
            LabeledCounter(
                text = item.text,
                onValueChange = {},
                modifier = Modifier.padding(start = 20.dp, end = 10.dp),
                enabled = false
            )
        }

        TemplateTypes.RATING_BAR -> {
            LabeledRatingBar(
                text = item.text,
                values = 5,
                onValueChange = {},
                modifier = Modifier.padding(start = 20.dp, end = 10.dp),
                enabled = false
            )
        }

        TemplateTypes.TEXT_FIELD -> {
            BasicInputField(
                icon = painterResource(id = R.drawable.ic_text_format_center),
                contentDescription = stringResource(id = R.string.ic_text_format_center_content_desc),
                hint = item.text,
                textFieldValue = TextFieldValue(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(0.9F),
                enabled = false
            )
        }

        TemplateTypes.CHECK_BOX -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.size(80.dp),
                    enabled = false
                )
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 10.dp, end = 20.dp)
                )
            }
        }

        TemplateTypes.PLAIN_TEXT -> {
            Text(
                text = item.text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 10.dp, end = 20.dp)
            )
        }

        TemplateTypes.TRI_SCORING -> {
            LabeledTriCounter(
                text1 = item.text,
                text2 = item.text2.toString(),
                text3 = item.text3.toString(),
                onValueChange1 = {},
                onValueChange2 = {},
                onValueChange3 = {},
                enabled = false
            )
        }

        TemplateTypes.TRI_BUTTON -> {
            TriButtonBlock(
                headerText = item.text,
                buttonLabelOne = item.text2.toString(),
                buttonLabelTwo = item.text3.toString(),
                buttonLabelThree = item.text4.toString(),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.padding(start = 30.dp, end = 15.dp, bottom = 10.dp)
            )
        }

        TemplateTypes.QUAD_BUTTON -> {
            QuadButtonBlock(
                headerText = item.text,
                buttonLabelOne = item.text2.toString(),
                buttonLabelTwo = item.text3.toString(),
                buttonLabelThree = item.text4.toString(),
                buttonLabelThree = item.text5.toString(),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.padding(start = 30.dp, end = 15.dp, bottom = 10.dp)
            )
        }

        TemplateTypes.IMAGE -> {
            EncodedImageComponent(
                base64Image = item.text,
                modifier = Modifier.padding(30.dp),
                editMode = true
            )
        }
    }
}
