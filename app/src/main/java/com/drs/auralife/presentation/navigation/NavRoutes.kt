package com.drs.auralife.presentation.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val LIBRARY = "library"
    const val HISTORY = "history"
    const val FILM_DETAILS = "film_details/{slug}"
    const val LIBRARY_DETAILS = "library_details/{name}"
    const val PAYMENT = "payment"
    const val PLAY_FILM = "play_film/{slug}"
    const val EXPLORE_DETAILS = "explore_details/{slug}/{name}"
    const val SEARCH = "search"

    fun filmDetails(slug: String) = "film_details/$slug"
    fun libraryDetails(name: String) = "library_details/$name"
    fun playFilm(slug: String) = "play_film/$slug"
    fun exploreDetails(slug: String, name: String) = "explore_details/$slug/$name"
}
