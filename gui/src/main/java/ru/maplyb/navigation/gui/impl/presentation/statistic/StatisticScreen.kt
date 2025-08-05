package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.util.calculateAzimuth
import ru.maplyb.navigation.gui.impl.util.format.formatDistance
import ru.maplyb.navigation.gui.impl.util.format.formatMillisecondsTime

@Composable
internal fun ColumnScope.StatisticScreen(
    statistic: StatisticModel?,
    onDismissRequest: () -> Unit,
    clear: () -> Unit,
    pause: () -> Unit,
    toSettings: () -> Unit
) {
    var showLog by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            modifier = Modifier
                .clickable {
                    toSettings()
                },
            tint = Color.White,
            imageVector = Icons.Default.Settings,
            contentDescription = null
        )
        Spacer(Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .clickable {
                    onDismissRequest()
                },
            tint = Color.White,
            imageVector = Icons.Default.Close,
            contentDescription = null
        )
    }

    Spacer(Modifier.height(8.dp))
    if (statistic != null) {
        ExpandRouteStatsBottomSheet(
            statistic = statistic,
            onPause = pause,
            onFinish = clear,
            changeShowLigState = {
                showLog = !showLog
            }
        )
    } else {
        Text(
            text = "Статистика пуста",
            fontSize = 24.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExpandRouteStatsBottomSheet(
    statistic: StatisticModel = StatisticModel.default(),
    onPause: () -> Unit = {},
    onFinish: () -> Unit = {},
    changeShowLigState: () -> Unit
) {
    var showFull by remember {
        mutableStateOf<Boolean>(true)
    }
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
        mutableStateOf("${statistic.averageSpeed}")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = statistic.lifecycle.ruName,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = statistic.lifecycle.color,
            )
        )
        Spacer(Modifier.height(16.dp))
        StatisticFields(
            "${azimuth}°" to "Азимут",
            distanceRemaining to "Осталось идти",
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        )
        Spacer(Modifier.height(16.dp))
        AnimatedVisibility(
            visible = showFull,
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StatisticFields(
                    averageSpeed to "Средняя скорость,км/ч",
                    distancePassed to "Пройдено",
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    )
                )
                Spacer(Modifier.height(16.dp))
                StatisticFields(
                    timeElapsed to "Время в пути",
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(16.dp))
            }
        }
        if (statistic.lifecycle != StatisticLifecycle.END) {
            val (pauseButtonText, pauseButtonTextColor) = when (statistic.lifecycle) {
                StatisticLifecycle.FORCE_PAUSE -> "Возобновить" to Color.Green
                else -> "Пауза" to Color(0xffFFB02C)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffEA1019),
                        contentColor = Color.White
                    ),
                    content = {
                        Text(
                            text = "Завершить"
                        )
                    },
                    onClick = {
                        onFinish()
                    }
                )
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pauseButtonTextColor,
                        contentColor = Color.Black
                    ),
                    content = {
                        Text(
                            text = pauseButtonText
                        )
                    },
                    onClick = {
                        onPause()
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        Rectangle { showFull = !showFull }
    }
}

@Composable
private fun Rectangle(
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(width = 28.dp, height = 3.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xffd4d4d4))
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRectangle() {
    Rectangle()
}

@Composable
private fun StatisticFields(
    vararg statistic: Pair<String, String>,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Row {
        statistic.forEach { value ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value.first,
                    style = textStyle.copy(
                        color = Color.White
                    )
                )
                Text(
                    text = value.second,
                    style = textStyle.copy(
                        fontSize = textStyle.fontSize / 1.5,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRouteStatsBottomSheet() {
    ExpandRouteStatsBottomSheet() {}
}
