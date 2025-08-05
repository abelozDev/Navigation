package ru.maplyb.navigation.gui.impl.presentation.navigation

import java.io.Serializable

internal sealed interface Route: Serializable {
    class Statistic: Route, Serializable
    class Settings: Route, Serializable
}


