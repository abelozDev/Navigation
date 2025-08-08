package ru.maplyb.navigation.gui.impl.presentation.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import java.io.Serializable

internal class Router(
    initialRoute: Route
): Serializable {

    private val _currentRouteStack: MutableState<List<Route>> = mutableStateOf(listOf(initialRoute))
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

internal val LocalCurrentRoute = staticCompositionLocalOf<Route> {
    error("No Route provided")
}
internal val LocalRouter = staticCompositionLocalOf<Router> {
    error("No Router provided")
}

