package com.drs.auralife.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.drs.auralife.navigation.NavRoutes

class AppNavigator(private val navController: NavController) {
    fun navigateTo(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
        navController.navigate(route, builder)
    }

    fun navigateToHome() {
        navController.navigate(NavRoutes.HOME) {
            popUpTo(NavRoutes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateToExplore() {
        navController.navigate(NavRoutes.EXPLORE) {
            popUpTo(NavRoutes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateToLibrary() {
        navController.navigate(NavRoutes.LIBRARY) {
            popUpTo(NavRoutes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateToHistory() {
        navController.navigate(NavRoutes.HISTORY) {
            popUpTo(NavRoutes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateToLogin() {
        navController.navigate(NavRoutes.LOGIN)
    }

    fun navigateToRegister() {
        navController.navigate(NavRoutes.REGISTER)
    }

    fun navigateToPayment() {
        navController.navigate(NavRoutes.PAYMENT)
    }

    fun navigateToSearch() {
        navController.navigate(NavRoutes.SEARCH)
    }

    fun navigateToFilmDetails(slug: String) {
        navController.navigate(NavRoutes.filmDetails(slug))
    }

    fun navigateToLibraryDetails(name: String) {
        navController.navigate(NavRoutes.libraryDetails(name))
    }

    fun navigateToPlayFilm(slug: String) {
        navController.navigate(NavRoutes.playFilm(slug))
    }

    fun navigateToExploreDetails(slug: String, name: String) {
        navController.navigate(NavRoutes.exploreDetails(slug, name))
    }

    fun navigateBack(): Boolean = navController.popBackStack()

    fun setOnDestinationChangedListener(listener: (destinationId: Int) -> Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            listener(destination.id)
        }
    }
}