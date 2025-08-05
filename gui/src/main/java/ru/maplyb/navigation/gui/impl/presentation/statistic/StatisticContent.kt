package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import ru.maplyb.navigation.gui.impl.data.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
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
    var sheetContentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetSwipeEnabled = true,
        sheetShadowElevation = 0.dp,
        sheetTonalElevation = 0.dp,
        sheetContainerColor = Color(0xff2C2A2A),
        sheetPeekHeight = sheetContentHeight + 56.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .background(Color(0xff2C2A2A))
                    .fillMaxWidth()
                    .wrapContentHeight()
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
                    tint = Color.White,
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
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

        }
    ) {}
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
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffCCCCCC),
                    contentColor = Color(0xff1c1c1c)
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
                        fontSize = textStyle.fontSize/1.5,
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
