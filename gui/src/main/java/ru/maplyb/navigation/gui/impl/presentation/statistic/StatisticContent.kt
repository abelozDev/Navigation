package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.util.calculateAzimuth
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticContent(
    sheetState: SheetState,
    statistic: StatisticModel?,
    onDismissRequest: () -> Unit,
    clear: () -> Unit,
    pause: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        if (statistic != null) {
            HaveStatistic(statistic, clear, pause)
        } else {
            Text(
                text = "Статистика пуста",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun HaveStatistic(
    statistic: StatisticModel,
    clear: () -> Unit,
    pause: () -> Unit
) {
    var averageSpeed by remember() {
        mutableDoubleStateOf(0.0)
    }
    var azimuth by remember() {
        mutableIntStateOf(0)
    }
    LaunchedEffect(statistic.startTime, statistic.leftToDo) {
        while (true) {
            delay(1000)

            val currentTime = System.currentTimeMillis()

            val durationMillis = currentTime - statistic.startTime
            val hours = durationMillis.toDouble() / (1000 * 60 * 60)
            averageSpeed = String.format("%.1f", (statistic.leftToDo / 1000) / hours).toDouble()
        }
    }
    LaunchedEffect(statistic.lastPosition) {
        azimuth = statistic.lastPosition?.let {
            calculateAzimuth(it, statistic.endPoint)
        } ?: 0
    }
    Column {
        Text(
            text = "Пройдено: ${statistic.leftToDo} м",
            fontSize = 24.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Осталось идти :${statistic.totalDistance} м",
            fontSize = 24.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Средняя скорость :${averageSpeed} км/ч",
            fontSize = 24.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Азимут: $azimuth°",
            fontSize = 24.sp
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    clear()
                },
                content = {
                    Text(
                        text = "Очистить",
                        fontSize = 24.sp
                    )
                }
            )
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    pause()
                },
                content = {
                    Text(
                        text = "Пауза",
                        fontSize = 24.sp
                    )
                }
            )
        }
    }
}