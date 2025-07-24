package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.maplyb.navigation.gui.impl.data.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.util.calculateAzimuth
import ru.maplyb.navigation.gui.impl.util.format.formatDistance
import ru.maplyb.navigation.gui.impl.util.format.formatMillisecondsTime
import ru.maplyb.navigation.gui.impl.util.format.formatTime
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun StatisticContent(
    logs: List<PositionDataModel>,
    sheetState: SheetState,
    statistic: StatisticModel?,
    onDismissRequest: () -> Unit,
    clear: () -> Unit,
    pause: () -> Unit
) {
    var showLog by remember {
        mutableStateOf(false)
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        if (statistic != null) {
            if (showLog) {
                LogsList(logs) {
                    showLog = !showLog
                }
            } else {
                RouteStatsBottomSheet(
                    statistic = statistic,
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
internal fun RouteStatsBottomSheet(
    statistic: StatisticModel = StatisticModel.default(),
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
        mutableStateOf("${statistic.leftToDo/1000.0}")
    }
    val timeElapsed by remember(statistic.travelTime) {
        mutableStateOf(formatMillisecondsTime(statistic.travelTime))
    }
    val distanceRemaining by remember(statistic.totalDistance) {
        mutableStateOf(formatDistance(statistic.totalDistance))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = {
                    changeShowLigState()
                }
            ),
            text = "Статистика маршрута",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        StatsRow(label = "Пройдено", value = distancePassed)
        StatsRow(label = "Время в пути", value = timeElapsed)
        StatsRow(label = "Осталось идти", value = distanceRemaining)
        //todo средняя скорость
        StatsRow(label = "Средняя скорость", value = "0.0")
        StatsRow(label = "Азимут", value = "${azimuth}°")

        Divider(modifier = Modifier.padding(top = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
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

@Composable
private fun StatsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
    RouteStatsBottomSheet() {}
}
