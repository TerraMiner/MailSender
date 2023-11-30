package ua.terra.gui.settings

import ua.terra.application.Instance
import ua.terra.gui.navigation.NavBarSection
import ua.terra.gui.dgrow
import ua.terra.gui.settings.profile.ProfileManager
import ua.terra.gui.titleBar
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class SettingsView : View("Mail Sender") {

    init {
        primaryStage.setOnCloseRequest {
            Instance.IsClosed = true
        }
    }

    override val root = borderpane {

        prefWidth = Instance.PrefWidth
        prefHeight = Instance.PrefHeight

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

                    titleBar("Settings") {
                        hbox{
                            hgrow = Priority.ALWAYS
                            alignment = Pos.TOP_RIGHT
                            spacing = 10.0
                            vbox {
                                alignment = Pos.CENTER
                                add(ProfileManager.buttonLoad)

                                text("Load") {
                                    style {
                                        fontSize = 12.px
                                        fill = Color.rgb(220, 220, 220)
                                    }
                                }
                            }

                            vbox {
                                alignment = Pos.CENTER
                                add(ProfileManager.buttonSave)

                                text("Save") {
                                    style {
                                        fontSize = 12.px
                                        fill = Color.rgb(220, 220, 220)
                                    }
                                }
                            }
                        }
                    }

                    hbox {
                        dgrow = Priority.ALWAYS

                        alignment = Pos.CENTER

                        vbox {
                            dgrow = Priority.ALWAYS

                            alignment = Pos.CENTER

                            spacing = 10.0

                            splitpane(Orientation.VERTICAL) {
                                dgrow = Priority.ALWAYS

                                maxWidth = 800.0
                                maxHeight = 700.0

                                hbox {
                                    splitpane(Orientation.HORIZONTAL) {
                                        hgrow = Priority.ALWAYS


                                        spacing = 10.0

                                        add(DatabaseSection)

                                        add(PropertySection)
                                    }
                                }

                                hbox {
                                    splitpane(Orientation.HORIZONTAL) {
                                        hgrow = Priority.ALWAYS

                                        spacing = 10.0

                                        add(SendersSection)

                                        add(ReceiversSection)
                                    }
                                }
                            }
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