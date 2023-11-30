package ua.terra.gui.home

import ua.terra.application.Instance
import ua.terra.application.Instance.PrefHeight
import ua.terra.application.Instance.PrefWidth
import ua.terra.application.Updater
import ua.terra.gui.navigation.NavBarSection
import ua.terra.gui.dgrow
import ua.terra.gui.titleBar
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

class HomeView : View("Mail Sender") {

    init {
        addStageIcon(Instance.AppIcon)
        primaryStage.setOnCloseRequest {
            Instance.IsClosed = true
        }
        Updater
    }

    override val root = borderpane {

        prefWidth = PrefWidth
        prefHeight = PrefHeight

        stylesheets.add("styles/style.css")

        center {
            dgrow = Priority.ALWAYS

            hbox {
                dgrow = Priority.ALWAYS

                padding = insets(10.0)

                spacing = 10.0

                vbox {
                    dgrow = Priority.ALWAYS

                    alignment = Pos.CENTER

                    spacing = 10.0

                    titleBar("Home")

                    hbox {
                        dgrow = Priority.ALWAYS

                        alignment = Pos.CENTER

                        vbox {
                            dgrow = Priority.ALWAYS
                            alignment = Pos.CENTER
                            spacing = 10.0

                            maxWidth = 800.0
                            maxHeight = 700.0


                                add(StatisticSection)

                                add(ControlsSection)

                                add(AccountInfoSection)


//                            }

                        }
                    }
                }
            }
        }


        bottom {
            add(NavBarSection())
        }
    }
}