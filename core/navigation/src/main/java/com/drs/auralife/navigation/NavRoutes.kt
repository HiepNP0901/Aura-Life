package com.drs.auralife.navigation

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
    const val PAYMENT = "payment"
    const val SEARCH = "search"

    fun filmDetails(slug: String) = "film_details/$slug"
    fun libraryDetails(name: String) = "library_details/$name"
    fun playFilm(slug: String) = "play_film/$slug"
    fun exploreDetails(slug: String, name: String) = "explore_details/$slug/$name"
}
