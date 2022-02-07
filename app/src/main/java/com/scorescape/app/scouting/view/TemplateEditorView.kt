package com.scorescape.app.scouting.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scorescape.app.scouting.NavDestination
import com.scorescape.app.scouting.R
import com.scorescape.app.scouting.components.*
import com.scorescape.app.scouting.model.TemplateItem
import com.scorescape.app.scouting.model.TemplateTypes
import com.scorescape.app.scouting.utilities.getViewModel
import com.scorescape.app.scouting.utilities.longPressEffect
import com.scorescape.app.scouting.view.dialog.EditTemplateDialog
import com.scorescape.app.scouting.view.sheet.TemplateListSheet
import com.scorescape.app.scouting.viewmodel.TemplateEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.move
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable

@Composable
@OptIn(ExperimentalMaterialApi::class,
    ExperimentalPagerApi::class)
fun TemplateEditorView(
    navController: NavController,
    type: String
) {

    val viewModel = navController.context.getViewModel(TemplateEditorViewModel::class.java)
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val pagerState = rememberPagerState(initialPage = 0)

    viewModel.currentTemplateType = type

    Surface {
        ModalBottomSheetLayout(
            sheetContent = {
                TemplateListSheet(viewModel = viewModel)
            },
            sheetState = bottomSheetState,
            sheetShape = MaterialTheme.shapes.large
        ) {
            Column {
                TemplateEditorHeader(
                    type = type,
                    viewModel = viewModel,
                    navController = navController,
                    pagerState = pagerState
                )
                if (type == "match") {
                    viewModel.apply { currentListResource = autoListItems }
                    HorizontalPager(
                        count = 3,
                        state = pagerState
                    ) {
                        TemplateEditorList(viewModel, bottomSheetState)
                    }
                } else {
                    viewModel.apply { currentListResource = pitListItems }
                    TemplateEditorList(viewModel, bottomSheetState)
                }
            }
            EditTemplateDialog(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TemplateEditorHeader(
    type: String,
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
            navController.navigate(NavDestination.TemplateSave)
        }
    )
    viewModel.apply {
        fun fontWeight(index: Int) = if (index == pagerState.currentPage) FontWeight.SemiBold else FontWeight.Normal
        if (type != "pit") {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.template_editor_auto_header),
                    style = MaterialTheme.typography.body2,
                    fontWeight = fontWeight(0),
                    modifier = Modifier
                        .clickable {
                            viewModel.apply { currentListResource = autoListItems }
                            async.launch { pagerState.animateScrollToPage(0) }
                        }
                        .padding(end = 15.dp)
                )
                Text(
                    text = stringResource(id = R.string.template_editor_tele_header),
                    style = MaterialTheme.typography.body2,
                    fontWeight = fontWeight(1),
                    modifier = Modifier
                        .clickable {
                            viewModel.apply { currentListResource = teleListItems }
                            async.launch { pagerState.animateScrollToPage(1) }
                        }
                        .padding(end = 15.dp)
                )
                Text(
                    text = stringResource(id = R.string.template_editor_endgame_header),
                    style = MaterialTheme.typography.body2,
                    fontWeight = fontWeight(2),
                    modifier = Modifier.clickable {
                        viewModel.apply { currentListResource = endgameListItems }
                        async.launch { pagerState.animateScrollToPage(2) }
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class)
fun TemplateEditorList(
    viewModel: TemplateEditorViewModel,
    sheetState: ModalBottomSheetState
) {
    val list = viewModel.currentListResource
    val coroutineScope = rememberCoroutineScope()
    val listReorderState = rememberReorderState()
    AnimatedVisibility(visible = true) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
                .reorderable(
                    state = listReorderState,
                    onMove = { old, new ->
                        list.move(old.index, new.index)
                    }
                ),
            state = listReorderState.listState
        ) {
            itemsIndexed(items = list, key = { _, item -> item.id }) { index, item ->
                var height by remember { mutableStateOf(0.dp) }
                var setHeight by remember { mutableStateOf(false) }
                DottedRoundBox(
                    modifier = Modifier
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { it * (index + 1) },
                                animationSpec = tween(1000)
                            )
                        )
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                        .detectReorderAfterLongPress(listReorderState)
                        .longPressEffect(listReorderState.offsetByKey(item.id))
                        .onGloballyPositioned { layoutCoordinates ->
                            if (!setHeight) {
                                setHeight = true
                                height = layoutCoordinates.size.height.let { it - (it * 0.3) }.dp
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
                            ListItemFromType(item = item)
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
                        color = MaterialTheme.colors.primaryVariant,
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
                modifier = Modifier.padding(start = 20.dp, end = 10.dp)
            )
        }
        TemplateTypes.RATING_BAR -> {
            LabeledRatingBar(
                text = item.text,
                values = 5,
                onValueChange = {},
                modifier = Modifier.padding(start = 20.dp, end = 10.dp)
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
            Row {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 10.dp, end = 20.dp)
                )
            }
        }
        TemplateTypes.PLAIN_TEXT -> {
            Text(
                text = item.text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 10.dp, end = 20.dp)
            )
        }
    }
}