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

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiNewChat
import net.minecraftforge.client.GuiIngameForge

class NbcGuiInGame(mc: Minecraft) : GuiIngameForge(mc) {
    private val persistentChatGui = NbcGuiNewChat(mc)

    override fun getChatGUI(): GuiNewChat = persistentChatGui
}