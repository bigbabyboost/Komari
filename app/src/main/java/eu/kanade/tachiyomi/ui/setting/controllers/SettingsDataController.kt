package eu.kanade.tachiyomi.ui.setting.controllers

import eu.kanade.tachiyomi.ui.setting.SettingsComposeController
import Komari.presentation.settings.ComposableSettings
import Komari.presentation.settings.screen.SettingsDataScreen

class SettingsDataController : SettingsComposeController() {
    override fun getComposableSettings(): ComposableSettings = SettingsDataScreen
}
