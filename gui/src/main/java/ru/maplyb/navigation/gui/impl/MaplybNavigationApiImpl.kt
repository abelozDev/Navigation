package ru.maplyb.navigation.gui.impl

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maplyb.navigation.gui.api.MaplybNavigationApi
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.api.model.RouteStatistic
import ru.maplyb.navigation.gui.impl.data.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.domain.model.StartRouteArgs
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.presentation.location.LibLocationManager
import ru.maplyb.navigation.gui.impl.presentation.statistic.StatisticContent
import ru.maplyb.navigation.gui.impl.service.NavigationService
import ru.maplyb.navigation.gui.impl.service.NotificationChannel

internal object MaplybNavigationApiImpl : MaplybNavigationApi {

    private var mService: NavigationService? = null
    private var mBound: Boolean = false
    private var locationListener: NavigationLocationListener? = null
    private var globalCurrentStatistic: StatisticModel? = null
    private var onStatisticChangedCallback: ((RouteStatistic) -> Unit)? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as NavigationService.LocalBinder
            mService = binder.getService()
            mBound = true
            locationListener?.let {
                mService?.setLocationListener(it)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mService = null
            mBound = false
        }
    }

    private lateinit var application: Application
    private lateinit var locationManager: LibLocationManager
    private lateinit var repository: StatisticRepository
    private val statisticVisibility: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val scope = CoroutineScope(Dispatchers.IO)

    override fun onStatisticChanged(callback: (RouteStatistic) -> Unit) {
        this.onStatisticChangedCallback = callback
    }

    override fun show() {
        statisticVisibility.value = true
    }

    override fun hide() {
        statisticVisibility.value = false
    }

    override fun init(activity: Activity) {
        application = activity.application
        repository = StatisticRepository.create(application)
        locationManager = LibLocationManager.create(application)
        //todo сделать логику продолжения отслеживания локации если путь уже начат. Возможно пора добавить сервис
    }

    override fun pause() {
        globalCurrentStatistic?.let {
            scope.launch {
                repository.forcePause(it.id)
            }
        }
    }

    override fun stopStatistic() {
        scope.launch {
            globalCurrentStatistic?.let {
                repository.stopStatistic(it.id)
            }
        }
    }

    override fun currentRouteEndPoint(): GeoPoint? {
        return if (globalCurrentStatistic?.lifecycle != StatisticLifecycle.END) globalCurrentStatistic?.endPoint else null
    }

    override fun resumeCurrentStatistic(locationListener: NavigationLocationListener) {
        scope.launch {
            repository.resumeCurrentStatistic()?.let {
                resumeRoute(endPoint = it.endPoint, statisticId = it.id, locationListener)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ShowStatistic() {
        var currentStatistic by rememberSaveable {
            mutableStateOf<StatisticModel?>(null)
        }
        var logs by remember {
            mutableStateOf<List<PositionDataModel>>(emptyList())
        }
        val visibility by statisticVisibility.collectAsState()
        LaunchedEffect(Unit) {
            repository.getLastStatistic()
                .distinctUntilChanged()
                .onEach { statistic ->
                    println("statistic changed: $statistic")
                    if (statistic?.lifecycle == StatisticLifecycle.END) {
                        stopService()
                    }
                    currentStatistic = statistic
                    globalCurrentStatistic = statistic
                    statistic?.let {
                        onStatisticChangedCallback?.invoke(it.toRouteStatistic())
                    }
                }
                .launchIn(this)
        }
        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded,
                skipHiddenState = true
            )
        )
        val sheetState = rememberModalBottomSheetState()
        LaunchedEffect(visibility) {
            if (visibility) {
                sheetState.partialExpand()
            } else {
                sheetState.hide()
            }
        }

        /**Костыль чтобы ComposeView не перекрывало карту*/
        if (visibility) {
            StatisticContent(
                scaffoldState = scaffoldState,
                statistic = currentStatistic,
                onDismissRequest = {
                    hide()
                },
                clear = {
                    scope.launch {
                        repository.clear()
                    }
                },
                pause = {
                    pause()
                },
                logs = logs
            )
        }
    }

    private fun resumeRoute(endPoint: GeoPoint, statisticId: Int, locationListener: NavigationLocationListener) {
        startService(StartRouteArgs(endPoint, statisticId), locationListener)
    }

    override fun startRoute(endPoint: GeoPoint, locationListener: NavigationLocationListener) {
        startService(StartRouteArgs(endPoint, null), locationListener)
    }

    private fun stopService() {
        if (mBound) {
            mService?.stopServiceFromClient()
            application.unbindService(connection)
            mBound = false
        }
        val intent = Intent(application, NavigationService::class.java)
        application.stopService(intent)
    }

    private fun startService(args: StartRouteArgs, locationListener: NavigationLocationListener) {
        stopService()
        NotificationChannel.create(application)
        val intent = Intent(application, NavigationService::class.java).run {
            putExtra(NavigationService.NAVIGATION_END_POINT, args)
        }
        application.startService(intent)
        if (!mBound) {
            application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        this@MaplybNavigationApiImpl.locationListener = locationListener
    }
}