package com.example.diagassistant.ui.navigation

sealed class Route(val route: String) {
    data object Live : Route("live")
    data object Dtcs : Route("dtcs")
    data object Report : Route("report")
    data object Assistant : Route("assistant")
}