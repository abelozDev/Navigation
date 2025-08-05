package ru.maplyb.navigation.gui.impl.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
internal fun NavigationScaffold(
    startDestination: Route,
    content: @Composable () -> Unit
) {
    val router = rememberSaveable { Router(startDestination) }
    val localCurrentRoute = router.currentRouteStack.value.last()
    CompositionLocalProvider(
        LocalRouter provides router,
        currentRoute provides localCurrentRoute
    ) {
        content()
    }
}