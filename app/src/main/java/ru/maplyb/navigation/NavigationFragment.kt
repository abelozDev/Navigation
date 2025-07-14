package ru.maplyb.navigation

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponent
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.engine.LocationEngineRequest
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.permissions.PermissionsListener
import org.maplibre.android.location.permissions.PermissionsManager
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import org.maplibre.android.util.TileServerOptions
import ru.maplyb.navigation.gui.api.MaplybNavigationApi
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint

class NavigationFragment : Fragment(R.layout.navigation_fragment), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var startRouteButton: Button
    private lateinit var deleteRouteButton: Button
    private lateinit var buttonsLayout: LinearLayout
    private lateinit var showRoute: Button
    private lateinit var composeView: ComposeView

    private lateinit var navigationLib: MaplybNavigationApi

    private var lastLocation: Location? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private lateinit var maplibreMap: MapLibreMap
    private var selectedLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        startRouteButton = view.findViewById(R.id.routeButton)
        deleteRouteButton = view.findViewById(R.id.deleteRoute)
        showRoute = view.findViewById(R.id.showRoute)
        buttonsLayout = view.findViewById(R.id.buttonsLayout)
        composeView = view.findViewById(R.id.composeView)

        mapView.getMapAsync { map ->
            map.setStyle("https://demotiles.maplibre.org/style.json")
            map.cameraPosition = CameraPosition.Builder().target(LatLng(0.0, 0.0)).zoom(1.0).build()
            map.addOnMapClickListener { location ->
                selectedLocation.value = location
                selectedLocation.value?.let {
                    addMarker(it)
                }
                true
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            selectedLocation
                .flowWithLifecycle(lifecycle)
                .collect {
                    buttonsLayout.isGone = it == null
                }
        }
        checkPermissions()
        navigationLib = MaplybNavigationApi.create()
        navigationLib.init(requireActivity())
        initViews()
    }
    fun initViews() {
        composeView.setContent {
            navigationLib.ShowStatistic()
        }
        deleteRouteButton.setOnClickListener {
            selectedLocation.value = null
            maplibreMap.clear()
        }
        startRouteButton.setOnClickListener {
            selectedLocation.value?.let {
                navigationLib.startRoute(
                    GeoPoint(
                        it.latitude,
                        it.longitude,
                        0.0
                    )
                ) { startLocation, endLocation ->
                    println(
                        "startLocation: $startLocation, \nendLocation: $endLocation"
                    )
                }
            }
        }
        showRoute.setOnClickListener {
            navigationLib.show()
        }
    }

    private fun addMarker(location: LatLng) {
        val options = MarkerOptions()
            .position(location)
            .snippet(location.latitude.toString() + "`, " + location.longitude.toString())
        maplibreMap.clear()
        maplibreMap.addMarker(options)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(maplibreMap: MapLibreMap) {
        this.maplibreMap = maplibreMap
        maplibreMap.setStyle("https://demotiles.maplibre.org/style.json") { style: Style ->
            locationComponent = maplibreMap.locationComponent
            val locationComponentOptions =
                LocationComponentOptions.builder(requireContext())
                    .pulseEnabled(true)
                    .build()
            val locationComponentActivationOptions =
                buildLocationComponentActivationOptions(style, locationComponentOptions)
            locationComponent!!.activateLocationComponent(locationComponentActivationOptions)
            locationComponent!!.isLocationComponentEnabled = true
            locationComponent!!.cameraMode = CameraMode.TRACKING
            locationComponent!!.forceLocationUpdate(lastLocation)
        }
    }

    private fun buildLocationComponentActivationOptions(
        style: Style,
        locationComponentOptions: LocationComponentOptions
    ): LocationComponentActivationOptions {
        return LocationComponentActivationOptions
            .builder(requireContext(), style)
            .locationComponentOptions(locationComponentOptions)
            .useDefaultLocationEngine(true)
            .locationEngineRequest(
                LocationEngineRequest.Builder(750)
                    .setFastestInterval(750)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .build()
            )
            .build()
    }

    private fun checkPermissions() {
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            mapView.getMapAsync(this)
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    Toast.makeText(
                        requireContext(),
                        "You need to accept location permissions.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        mapView.getMapAsync(this@NavigationFragment)
                    } else {
                        requireActivity().finish()
                    }
                }
            })
            permissionsManager!!.requestLocationPermissions(requireActivity())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}