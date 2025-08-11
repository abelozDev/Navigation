package ru.maplyb.navigation.gui.impl.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

internal sealed interface Route: Parcelable {
    @Parcelize
    class Statistic: Route
    @Parcelize
    class Settings: Route, Serializable
}


