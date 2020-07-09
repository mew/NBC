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

package zone.nora.nbc.gui

import club.sk1er.elementa.components.Window
import net.minecraft.client.gui.GuiChat
import org.lwjgl.input.Mouse
import zone.nora.nbc.Nbc
import zone.nora.nbc.gui.components.ChatWindowComponent

class NbcChatGui : GuiChat() {
    override fun initGui() {
        if (window.childrenOfType<ChatWindowComponent>().isEmpty()) Nbc.configuration.forEach { window.addChild(ChatWindowComponent(it)) }
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // nothing
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        window.mouseClick(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        window.mouseRelease()
        super.mouseReleased(mouseX, mouseY, state)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) =
        if (keyCode == 1) mc.displayGuiScreen(null) else window.keyType(typedChar, keyCode)

    override fun handleMouseInput() {
        super.handleMouseInput()
        val eventDWheel = Mouse.getEventDWheel()
        if (eventDWheel != 0) {
            window.mouseScroll(eventDWheel.coerceIn(-1, 1))
        }
    }

    companion object {
        val window = Window()
    }
}