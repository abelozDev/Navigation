package ru.maplyb.navigation.gui.impl.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import ru.maplyb.navigation.gui.impl.data.local.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.presentation.navigation.LocalRouter
import ru.maplyb.navigation.gui.impl.presentation.navigation.NavigationScaffold
import ru.maplyb.navigation.gui.impl.presentation.navigation.Route
import ru.maplyb.navigation.gui.impl.presentation.navigation.currentRoute
import ru.maplyb.navigation.gui.impl.presentation.settings.SettingsScreen
import ru.maplyb.navigation.gui.impl.presentation.statistic.StatisticScreen


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun MainBottomScaffold(
    logs: List<PositionDataModel>,
    scaffoldState: BottomSheetScaffoldState,
    pauseState: Boolean,
    statistic: StatisticModel?,
    updatePauseState: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    clear: () -> Unit,
    pause: () -> Unit,
) {
    var sheetContentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetSwipeEnabled = true,
        sheetShadowElevation = 0.dp,
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetTonalElevation = 0.dp,
        sheetContainerColor = Color(0xff2C2A2A),
        sheetDragHandle = null,
        sheetPeekHeight = sheetContentHeight,
        sheetContent = {
            NavigationScaffold(
                startDestination = Route.Statistic()
            ) {
                val router = LocalRouter.current
                Column(
                    modifier = Modifier
                        .background(Color(0xff2C2A2A))
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .onGloballyPositioned { layoutCoordinates ->
                            sheetContentHeight = with(density) {
                                layoutCoordinates.size.height.toDp()
                            }
                        }
                        .padding(16.dp)
                ) {
                    when (currentRoute.current) {
                        is Route.Settings -> {
                            SettingsScreen(
                                onDismissRequest = onDismissRequest,
                                pop = {
                                    router.pop()
                                },
                                pauseState = pauseState,
                                updatePauseState = updatePauseState
                            )
                        }
                        is Route.Statistic -> {
                            StatisticScreen(
                                statistic = statistic,
                                onDismissRequest = onDismissRequest,
                                clear = clear,
                                pause = pause,
                                toSettings = {
                                    router.push(Route.Settings())
                                }
                            )
                        }
                    }
                }
            }
        }
    ) {}
}