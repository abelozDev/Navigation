package ru.maplyb.navigation.gui.impl

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maplyb.navigation.gui.api.MaplybNavigationApi
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.domain.model.StartRouteArgs
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.presentation.location.LibLocationManager
import ru.maplyb.navigation.gui.impl.presentation.statistic.StatisticContent
import ru.maplyb.navigation.gui.impl.service.NavigationService
import ru.maplyb.navigation.gui.impl.service.NotificationChannel

internal object MaplybNavigationApiImpl : MaplybNavigationApi {

    private lateinit var mService: NavigationService
    private var mBound: Boolean = false
    private var locationListener: NavigationLocationListener? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as NavigationService.LocalBinder
            mService = binder.getService()
            mBound = true
            locationListener?.let {
                mService.setLocationListener(it)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private lateinit var application: Application
    private lateinit var locationManager: LibLocationManager
    private lateinit var repository: StatisticRepository
    private val statisticVisibility: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val scope = CoroutineScope(Dispatchers.IO)

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

    @OptIn(ExperimentalMaterial3Api::class)
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
        val sheetState = rememberModalBottomSheetState()
        LaunchedEffect(visibility) {
            if (visibility) {
                sheetState.show()
            } else {
                sheetState.hide()
            }
        }
        /**Костыль чтобы ComposeView не перекрывало карту*/
        if (visibility) {
            StatisticContent(
                sheetState = sheetState,
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
                    //не может быть null
                    currentStatistic?.let {
                        scope.launch {
                            repository.pause(it.id)
                        }
                    }

                }
            )
        }
    }

    override fun startRoute(endPoint: GeoPoint, locationListener: NavigationLocationListener) {
        scope.launch {
            val isPossible = repository.getCurrentStatistic().first() == null
            if (isPossible) {
                withContext(Dispatchers.Main) {
                    NotificationChannel.create(application)
                    val intent = Intent(application, NavigationService::class.java).run {
                        putExtra(NavigationService.NAVIGATION_END_POINT, StartRouteArgs(endPoint))
                    }
                    application.startService(intent)
                    if (!mBound) {
                        application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
                    }
                    this@MaplybNavigationApiImpl.locationListener = locationListener
                }
            }
        }
    }
}