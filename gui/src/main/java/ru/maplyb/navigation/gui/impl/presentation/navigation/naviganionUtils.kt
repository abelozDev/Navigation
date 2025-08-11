package ru.maplyb.navigation.gui.impl.presentation.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.graphics.times
import java.io.Serializable

internal class Router(
    vararg initialRoute: Route
) {

    private val _currentRouteStack: MutableState<List<Route>> = mutableStateOf(initialRoute.toList())
    val currentRouteStack: State<List<Route>> = _currentRouteStack


    fun push(route: Route) {
        _currentRouteStack.value = _currentRouteStack.value + route
    }

    fun pop() {
        if (_currentRouteStack.value.size > 1) {
            _currentRouteStack.value = _currentRouteStack.value.dropLast(1)
        }
    }
}

internal val LocalCurrentRoute = compositionLocalOf<Route> {
    error("No Route provided")
}
internal val LocalRouter = compositionLocalOf<Router> {
    error("No Router provided")
}

