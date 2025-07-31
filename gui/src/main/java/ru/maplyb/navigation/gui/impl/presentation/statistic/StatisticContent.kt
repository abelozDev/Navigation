package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.layout.TestModifierUpdaterLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.maplyb.navigation.gui.impl.data.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.ui.icons.iconCollapsed
import ru.maplyb.navigation.gui.impl.ui.icons.iconExpand
import ru.maplyb.navigation.gui.impl.util.calculateAzimuth
import ru.maplyb.navigation.gui.impl.util.format.formatDistance
import ru.maplyb.navigation.gui.impl.util.format.formatMillisecondsTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun StatisticContent(
    logs: List<PositionDataModel>,
    scaffoldState: BottomSheetScaffoldState,
    statistic: StatisticModel?,
    onDismissRequest: () -> Unit,
    clear: () -> Unit,
    pause: () -> Unit
) {
    var showLog by remember {
        mutableStateOf(false)
    }
    var expand by remember {
        mutableStateOf<Boolean>(true)
    }
    var sheetContentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetSwipeEnabled = true,
        sheetShadowElevation = 0.dp,
        sheetTonalElevation = 0.dp,
        sheetPeekHeight = sheetContentHeight + 56.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 16.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        sheetContentHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            onDismissRequest()
                        },
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Статистика маршрута",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    ExpandableIcon(expand) {
                        expand = !expand
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (statistic != null) {
                    if (showLog) {
                        LogsList(logs) {
                            showLog = !showLog
                        }
                    } else {
                        ExpandRouteStatsBottomSheet(
                            statistic = statistic,
                            showFull = expand,
                            onPause = pause,
                            onFinish = clear,
                            changeShowLigState = {
                                showLog = !showLog
                            }
                        )
                    }
                } else {
                    Text(
                        text = "Статистика пуста",
                        fontSize = 24.sp
                    )
                }
            }

        }
    ) {}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ExpandableIcon(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = {
            fadeIn() with fadeOut()
        },
        label = "ExpandCollapseAnimation"
    ) { targetExpanded ->
        Icon(
            modifier = Modifier
                .clickable {
                onClick()
            },
            imageVector = if (targetExpanded) iconCollapsed() else iconExpand(),
            contentDescription = if (targetExpanded) "Collapse" else "Expand"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LogsList(
    logs: List<PositionDataModel>,
    changeShowLigState: () -> Unit
) {
    Column {
        Text(
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = {
                    changeShowLigState()
                }
            ),
            text = "Логи передвижения",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        LazyColumn {
            items(logs.size) {
                val item = logs[it]
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "${item.type.name} "
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExpandRouteStatsBottomSheet(
    statistic: StatisticModel = StatisticModel.default(),
    showFull: Boolean = true,
    onPause: () -> Unit = {},
    onFinish: () -> Unit = {},
    changeShowLigState: () -> Unit
) {
    var azimuth by remember() {
        mutableIntStateOf(0)
    }
    LaunchedEffect(statistic.lastPosition) {
        azimuth = statistic.lastPosition?.let {
            calculateAzimuth(it, statistic.endPoint)
        } ?: 0
    }
    val distancePassed by remember(statistic.leftToDo) {
        mutableStateOf(formatDistance(statistic.leftToDo))
    }
    val timeElapsed by remember(statistic.travelTime) {
        mutableStateOf(formatMillisecondsTime(statistic.travelTime))
    }
    val distanceRemaining by remember(statistic.totalDistance) {
        mutableStateOf(formatDistance(statistic.totalDistance))
    }
    val averageSpeed by remember(statistic.averageSpeed) {
        mutableStateOf("${statistic.averageSpeed}км/ч")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        StatsRow(label = "Осталось идти", value = distanceRemaining)
        StatsRow(label = "Азимут", value = "${azimuth}°")
        AnimatedVisibility(
            visible = showFull,
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                StatsRow(label = "Пройдено", value = distancePassed)
                StatsRow(label = "Время в пути", value = timeElapsed)
                StatsRow(label = "Средняя скорость", value = averageSpeed)
                Divider(modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                ) {
                    OutlinedButton(
                        onClick = onPause,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Остановить")
                    }

                    Button(
                        onClick = onFinish,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Закончить")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun PreviewRouteStatsBottomSheet() {
    ExpandRouteStatsBottomSheet() {}
}
