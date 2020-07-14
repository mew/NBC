/*
 * Nora's Better Chat
 * Copyright (C) 2020 Nora Cos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zone.nora.nbc.gui.config

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import zone.nora.nbc.Nbc
import zone.nora.nbc.gson.SerializedChatTab
import zone.nora.nbc.gson.SerializedChatWindow
import zone.nora.nbc.gui.chat.NbcChatGui
import zone.nora.nbc.gui.components.*
import java.awt.Color

// This may be the worst code I have ever written in my entire life.
class ConfigGui : WindowScreen() {
    private val configWindow: ConfigWindowComponent = ConfigWindowComponent("Nora's Better Chat").constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        height = 100.pixels()
        width = 100.pixels()
    } childOf window

    private var chatWindowConfiguration = Nbc.configuration
    private var currentWindow = 0

    init {
        main()
    }

    private fun main() {
        newPage(125, 225, "Nora's Better Chat", 0)
        configWindow.bar.animate {
            setColorAnimation(Animations.IN_OUT_SIN, .2f, Color(255, 165, 0).asConstraint())
        }

        var selected: Int? = null

        val selector = UIBlock(Color(30, 30, 30)).constrain {
            height = RelativeConstraint(.7f)
            width = RelativeConstraint(.9f)
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf configWindow.body

        val editButton = ConfigOptionButtonComponent("Edit", false).constrain {
            x = CenterConstraint()
            y = 10.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }

        val deleteButton = ConfigOptionButtonComponent("Delete", false, Color(255, 0, 0)).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }

        fun loadWindows() {
            selector.clearChildren()
            for (i in chatWindowConfiguration.indices) {
                ConfigSelectorValueComponent(chatWindowConfiguration[i].title, i).onMouseClick {
                    selected = (this as ConfigSelectorValueComponent).id
                    editButton.changeState(true)
                    deleteButton.changeState(true)
                } childOf selector
            }
        }

        ConfigOptionButtonComponent("Add", highlightColour = Color(0, 255, 0)).constrain {
            x = 10.pixels()
            y = 10.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }.onMouseClick {
            val chatWindow = SerializedChatWindow()
            chatWindow.tabs.add(SerializedChatTab())
            chatWindowConfiguration.add(chatWindow)
            loadWindows()
        } childOf configWindow.body

        deleteButton.onMouseClick {
            if (selected == null) return@onMouseClick
            if ((this as ConfigOptionButtonComponent).enabled) {
                // fuckkkkkk null pointers. if null pointer here pls report.
                chatWindowConfiguration.remove(chatWindowConfiguration[selected!!])
                loadWindows()
                this.changeState(false)
                editButton.changeState(false)
                selected = null
            }
        }

        editButton.onMouseClick {
            if (selected == null) return@onMouseClick
            if ((this as ConfigOptionButtonComponent).enabled) editWindow(chatWindowConfiguration[selected!!])
        }

        editButton childOf configWindow.body
        deleteButton childOf configWindow.body

        loadWindows()
    }

    private fun editWindow(chatWindow: SerializedChatWindow) {
        newPage(150, 200, chatWindow.title, 1)
        configWindow.bar.animate {
            setColorAnimation(Animations.IN_OUT_SIN, .2f, chatWindow.colour.getColour().asConstraint())
        }

        UIText("Window Title:").constrain {
            x = CenterConstraint()
            y = 5.pixels()
        } childOf configWindow.body

        val chatWindowTitle = ConfigTextInputComponent(135f, chatWindow.title) childOf configWindow.body

        ConfigOptionButtonComponent("Edit Window Constraints").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }.onMouseClick {
            editConstraints(chatWindow)
        } childOf configWindow.body

        ConfigOptionButtonComponent("Edit Window Colour").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }.onMouseClick {
            editColour(chatWindow)
        } childOf configWindow.body

        ConfigOptionButtonComponent("Edit Window Tabs").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }.onMouseClick {
            editTabs(chatWindow)
        } childOf configWindow.body

        ConfigBackButtonComponent().onMouseClick { main() } childOf configWindow.body

        configWindow.onMouseClick { e ->
            e.stopPropagation()
            if (currentWindow == 1) {
                chatWindowTitle.textInput.active = false
                if (chatWindowTitle.textInput.text.isNotEmpty()) {
                    chatWindow.title = chatWindowTitle.textInput.text
                    configWindow.bar.setTitle(chatWindow.title)
                }
            }
        }
    }

    private fun editConstraints(chatWindow: SerializedChatWindow) {
        newPage(150, 200, "${chatWindow.title} Constraints", 2)

        UIText("X Position (pixels):").constrain {
            x = CenterConstraint()
            y = 5.pixels()
        } childOf configWindow.body

        val xPosition = ConfigTextInputComponent(135f, chatWindow.constraints.x.position.toString(), TextInputComponent.Restrictions.FLOAT) childOf configWindow.body

        ConfigToggleButtonComponent("Align Opposite (X)", chatWindow.constraints.x.alignOpposite).onMouseClick {
            chatWindow.constraints.x.alignOpposite = (this as ConfigToggleButtonComponent).value
        } childOf configWindow.body

        UIText("Y Position (pixels):").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        val yPosition = ConfigTextInputComponent(135f, chatWindow.constraints.y.position.toString(), TextInputComponent.Restrictions.FLOAT) childOf configWindow.body

        ConfigToggleButtonComponent("Align Opposite (Y)", chatWindow.constraints.x.alignOpposite).onMouseClick {
            chatWindow.constraints.y.alignOpposite = (this as ConfigToggleButtonComponent).value
        } childOf configWindow.body

        UIText("Chat Window Width (pixels):").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        val chatWindowWidth = ConfigTextInputComponent(135f, chatWindow.constraints.width.toString(), TextInputComponent.Restrictions.FLOAT) childOf configWindow.body

        UIText("Chat Window Height (pixels):").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        val chatWindowHeight = ConfigTextInputComponent(135f, chatWindow.constraints.height.toString(), TextInputComponent.Restrictions.FLOAT) childOf configWindow.body

        ConfigBackButtonComponent().onMouseClick { editWindow(chatWindow) } childOf configWindow.body

        configWindow.onMouseClick { e ->
            e.stopPropagation()
            if (currentWindow == 2) {
                xPosition.textInput.active = false
                yPosition.textInput.active = false
                chatWindowWidth.textInput.active = false
                chatWindowHeight.textInput.active = false
                if (xPosition.textInput.text.isEmpty()) xPosition.textInput.text = "0.0"
                if (yPosition.textInput.text.isEmpty()) yPosition.textInput.text = "0.0"
                if (chatWindowHeight.textInput.text.isEmpty()) chatWindowHeight.textInput.text = "0.0"
                if (chatWindowWidth.textInput.text.isEmpty()) chatWindowWidth.textInput.text = "0.0"

                chatWindow.constraints.x.position = xPosition.textInput.text.toFloat()
                chatWindow.constraints.y.position = yPosition.textInput.text.toFloat()
                chatWindow.constraints.height = chatWindowHeight.textInput.text.toFloat()
                chatWindow.constraints.width = chatWindowWidth.textInput.text.toFloat()
            }
        }
    }

    private fun editTabs(chatWindow: SerializedChatWindow) {
        newPage(125, 240, "${chatWindow.title} Tabs", 3)

        var selected: Int? = null

        val selector = UIBlock(Color(30, 30, 30)).constrain {
            height = RelativeConstraint(.7f)
            width = RelativeConstraint(.9f)
            x = CenterConstraint()
            y = 8.pixels()
        } childOf configWindow.body

        val editButton = ConfigOptionButtonComponent("Edit", false).constrain {
            x = CenterConstraint()
            y = 40.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }

        val deleteButton = ConfigOptionButtonComponent("Delete", false, Color(255, 0, 0)).constrain {
            x = 10.pixels(true)
            y = 40.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }

        fun loadTabs() {
            selector.clearChildren()
            for (i in chatWindow.tabs.indices) {
                ConfigSelectorValueComponent(chatWindow.tabs[i].title, i).onMouseClick {
                    selected = (this as ConfigSelectorValueComponent).id
                    editButton.changeState(true)
                    deleteButton.changeState(chatWindow.tabs.size > 1)
                } childOf selector
            }
        }

        ConfigOptionButtonComponent("Add", highlightColour = Color(0, 255, 0)).constrain {
            x = 10.pixels()
            y = 40.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }.onMouseClick {
            val chatTab = SerializedChatTab()
            chatWindow.tabs.add(chatTab)
            loadTabs()
        } childOf configWindow.body

        deleteButton.onMouseClick {
            if (selected == null) return@onMouseClick
            if ((this as ConfigOptionButtonComponent).enabled) {
                chatWindow.tabs.remove(chatWindow.tabs[selected!!])
                loadTabs()
                this.changeState(false)
                editButton.changeState(false)
                selected = null
            }
        }

        editButton.onMouseClick {
            if (selected == null) return@onMouseClick
            if ((this as ConfigOptionButtonComponent).enabled) editTab(chatWindow, chatWindow.tabs[selected!!])
        }

        editButton childOf configWindow.body
        deleteButton childOf configWindow.body

        ConfigBackButtonComponent().onMouseClick { editWindow(chatWindow) } childOf configWindow.body

        loadTabs()
    }

    private fun editTab(chatWindow: SerializedChatWindow, chatTab: SerializedChatTab) {
        newPage(150, 200, "${chatTab.title} Tab", 4)

        UIText("Name:").constrain {
            x = CenterConstraint()
            y = 5.pixels()
        } childOf configWindow.body

        val chatTabTitle = ConfigTextInputComponent(135f, chatTab.title) childOf configWindow.body

        ConfigOptionButtonComponent("Edit Tab Filters").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }.onMouseClick {
            editFilters(chatWindow, chatTab)
        } childOf configWindow.body

        ConfigToggleButtonComponent("Hide Messages in CF-A", chatTab.hideInAll).onMouseClick {
            chatTab.hideInAll = (this as ConfigToggleButtonComponent).value
        } childOf configWindow.body

        UIText("Chat Prefix:").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        val chatTabPrefix = ConfigTextInputComponent(135f, chatTab.prefix) childOf configWindow.body

        ConfigBackButtonComponent().onMouseClick { editTabs(chatWindow) } childOf configWindow.body

        configWindow.onMouseClick { e ->
            e.stopPropagation()
            if (currentWindow == 4) {
                chatTabTitle.textInput.active = false
                chatTabPrefix.textInput.active = false
                chatTab.title = chatTabTitle.textInput.text
                chatTab.prefix = chatTabPrefix.textInput.text
            }
        }
    }

    private fun editColour(chatWindow: SerializedChatWindow) {
        newPage(150, 200, "${chatWindow.title} Colour", 5)

        UIText("\u00a7cRed\u00a7f (0-255):").constrain {
            x = CenterConstraint()
            y = 5.pixels()
        } childOf configWindow.body

        val red = ConfigTextInputComponent(135f, chatWindow.colour.red.toString(), TextInputComponent.Restrictions.COLOUR) childOf configWindow.body

        UIText("\u00a7aGreen\u00a7f (0-255):").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        val green = ConfigTextInputComponent(135f, chatWindow.colour.green.toString(), TextInputComponent.Restrictions.COLOUR) childOf configWindow.body

        UIText("\u00a79Blue\u00a7f (0-255):").constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        val blue = ConfigTextInputComponent(135f, chatWindow.colour.blue.toString(), TextInputComponent.Restrictions.COLOUR) childOf configWindow.body

        configWindow.onMouseClick { e ->
            e.stopPropagation()
            if (currentWindow == 5) {
                red.textInput.active = false
                green.textInput.active = false
                blue.textInput.active = false
                if (red.textInput.text.isEmpty()) red.textInput.text = "0"
                if (green.textInput.text.isEmpty()) green.textInput.text = "0"
                if (blue.textInput.text.isEmpty()) blue.textInput.text = "0"

                chatWindow.colour.red = red.textInput.text.toInt()
                chatWindow.colour.green = green.textInput.text.toInt()
                chatWindow.colour.blue = blue.textInput.text.toInt()
                configWindow.bar.setColor(chatWindow.colour.getColour().asConstraint())
            }
        }

        ConfigBackButtonComponent().onMouseClick { editWindow(chatWindow) } childOf configWindow.body
    }

    private fun editFilters(chatWindow: SerializedChatWindow, chatTab: SerializedChatTab) {
        newPage(125, 300, "${chatTab.title} Filters", 6)

        var selected: Int? = null

        val selector = UIBlock(Color(30, 30, 30)).constrain {
            height = RelativeConstraint(.7f)
            width = RelativeConstraint(.9f)
            x = CenterConstraint()
            y = 8.pixels()
        } childOf configWindow.body

        val deleteButton = ConfigOptionButtonComponent("Delete", false, Color(255, 0, 0)).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }

        fun loadFilter() {
            selector.clearChildren()
            for (i in chatTab.filter.indices) {
                ConfigSelectorValueComponent(chatTab.filter[i], i).onMouseClick {
                    selected = (this as ConfigSelectorValueComponent).id
                    deleteButton.changeState(true)
                } childOf selector
            }
        }

        deleteButton.onMouseClick {
            if (selected == null) return@onMouseClick
            if ((this as ConfigOptionButtonComponent).enabled) {
                chatTab.filter.remove(chatTab.filter[selected!!])
                loadFilter()
                changeState(false)
                selected = null
            }
        }

        deleteButton childOf configWindow.body

        val filter = ConfigTextInputComponent(112.5f, "", TextInputComponent.Restrictions.NONE).constrain {
            y = SiblingConstraint(5f)
        } childOf configWindow.body

        ConfigOptionButtonComponent("Add", highlightColour = Color(0, 255, 0)).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }.onMouseClick {
            filter.textInput.active = false
            if (filter.textInput.text.isNotEmpty()) {
                chatTab.filter.add(filter.textInput.text)
                filter.textInput.text = ""
            }
            loadFilter()
        } childOf configWindow.body

        configWindow.onMouseClick { e ->
            e.stopPropagation()
            if (currentWindow == 6) filter.textInput.active = false
        }

        ConfigBackButtonComponent().onMouseClick { editTab(chatWindow, chatTab) } childOf configWindow.body

        loadFilter()
    }

    private fun newPage(width: Int, height: Int, windowTitle: String, id: Int) {
        configWindow.body.clearChildren()
        configWindow.animate {
            setWidthAnimation(Animations.LINEAR, .25f, width.pixels())
            setHeightAnimation(Animations.LINEAR, .25f, height.pixels())
        }
        configWindow.bar.setTitle(windowTitle)
        currentWindow = id
    }

    override fun onGuiClosed() {
        Nbc.refreshChatConfig()
        NbcChatGui.window.clearChildren()
        NbcChatGui.addWindows()
        Nbc.putChatMessage("ok!")
    }
}