package com.merteroglu286.leitnerbox.utility

import androidx.navigation.NavDirections


sealed class NavigationCommand {
    data class ToDirection(val directions: NavDirections) : NavigationCommand()
    data object Back : NavigationCommand()
}