package ua.terra.gui.navigation

import ua.terra.gui.console.ConsoleView
import ua.terra.gui.home.HomeView
import ua.terra.gui.settings.SettingsView
import tornadofx.View
import kotlin.reflect.KClass

enum class ViewType(val clazz: KClass<out View>) {
    HOME(HomeView::class),
    SETTINGS(SettingsView::class),
    CONSOLE(ConsoleView::class),
}