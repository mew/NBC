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
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import zone.nora.nbc.Nbc
import zone.nora.nbc.gson.SerializedChatTab
import zone.nora.nbc.gson.SerializedChatWindow
import zone.nora.nbc.gui.components.ConfigChatWindowComponent
import zone.nora.nbc.gui.components.ConfigOptionButtonComponent
import zone.nora.nbc.gui.components.ConfigWindowComponent
import java.awt.Color

class ConfigGui : WindowScreen() {
    var chatWindowConfiguration = Nbc.configuration
    var selected: Int? = null
    private val selector: UIBlock
    private val editButton: ConfigOptionButtonComponent
    private val deleteButton: ConfigOptionButtonComponent

    init {
        val mainWindow = ConfigWindowComponent("Nora's Better Chat").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            height = 225.pixels()
            width = 125.pixels()
        } childOf window

        selector = UIBlock(Color(30, 30, 30)).constrain {
            height = RelativeConstraint(.7f)
            width = RelativeConstraint(.9f)
            x = 5.pixels()
            y = CenterConstraint()
        } childOf mainWindow.body

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
        } childOf mainWindow.body

        editButton = ConfigOptionButtonComponent("Edit", false).constrain {
            x = CenterConstraint()
            y = 10.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        } childOf mainWindow.body

        deleteButton = ConfigOptionButtonComponent("Delete", false, Color(255, 0, 0)).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
            height = 15.pixels()
            width = 30.pixels()
        }.onMouseClick {
            if (selected == null) return@onMouseClick
            if ((this as ConfigOptionButtonComponent).enabled) {
                // fuckkkkkk null pointers. if null pointer here pls report.
                chatWindowConfiguration.remove(chatWindowConfiguration[selected!!])
                loadWindows()
                this.changeState(false)
                editButton.changeState(false)
                selected = null
            }
        } as ConfigOptionButtonComponent childOf mainWindow.body

        loadWindows()
    }

    private fun loadWindows() {
        selector.clearChildren()
        for (i in chatWindowConfiguration.indices) {
            ConfigChatWindowComponent(chatWindowConfiguration[i].title, i).onMouseClick {
                selected = (this as ConfigChatWindowComponent).id
                editButton.changeState(true)
                deleteButton.changeState(true)
            } childOf selector
        }
    }
}