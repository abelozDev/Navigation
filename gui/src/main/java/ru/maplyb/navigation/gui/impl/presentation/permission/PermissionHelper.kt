package ru.maplyb.navigation.gui.impl.presentation.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


internal class PermissionHelper {

    private val defaultLocationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    internal fun checkLocationPermission(context: Context, permissions: List<String> = defaultLocationPermissions): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}