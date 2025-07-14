package ru.maplyb.navigation.gui.impl

import android.app.Activity
import android.app.Application
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maplyb.navigation.gui.api.MaplybNavigationApi
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.presentation.location.LibLocationManager
import ru.maplyb.navigation.gui.impl.presentation.statistic.StatisticContent
import ru.maplyb.navigation.gui.impl.util.log

internal object MaplybNavigationApiImpl : MaplybNavigationApi {

    private lateinit var application: Application
    private lateinit var locationManager: LibLocationManager
    private lateinit var repository: StatisticRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val statisticVisibility: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun show() {
        statisticVisibility.value = true
    }

    override fun hide() {
        statisticVisibility.value = false
    }

    override fun init(activity: Activity) {
        application = activity.application
        repository = StatisticRepository.create(application)
        locationManager = LibLocationManager(application)
    }

    @Composable
    override fun ShowStatistic() {
        var currentStatistic by rememberSaveable {
            mutableStateOf<StatisticModel?>(null)
        }
        val visibility by statisticVisibility.collectAsState()
        LaunchedEffect(Unit) {
            repository.getCurrentStatistic()
                .onEach {
                    currentStatistic = it
                }
                .launchIn(this)
        }
        if (visibility) {
            StatisticContent(
                statistic = currentStatistic,
                onDismissRequest = {
                    hide()
                }
            )
        }
    }

    override fun startRoute(endPoint: GeoPoint, locationListener: NavigationLocationListener) {
        var startLocation: Location? = null
        scope.launch {
            val isCreatePossible = repository.checkStartRouteIsPossible()
            if (!isCreatePossible) throw IllegalStateException("Route already started")
            var newStatistic: StatisticModel = repository.createEmptyStatistic()
            withContext(Dispatchers.Main) {
                locationManager
                    .init { location ->
                        if (startLocation == null) startLocation = location
                        locationListener.locationUpdated(
                            startLocation = GeoPoint(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                altitude = location.altitude
                            ),
                            endLocation = endPoint
                        )
                    }
                    .onFailure { log(it.message.toString()) }
            }
        }
    }
}