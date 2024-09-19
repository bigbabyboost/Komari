package eu.kanade.tachiyomi.ui.setting.controllers

import eu.kanade.tachiyomi.ui.setting.SettingsComposeController
import komari.presentation.settings.ComposableSettings
import komari.presentation.settings.screen.SettingsDataScreen

class SettingsDataController : SettingsComposeController() {
    override fun getComposableSettings(): ComposableSettings = SettingsDataScreen
}
