package ci.nsu.moble.main

import androidx.annotation.StringRes

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int? = null  // Сделал resourceId опциональным
) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}