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

package zone.nora.nbc.command

import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import zone.nora.nbc.Nbc
import zone.nora.nbc.gui.chat.NbcChatGui
import zone.nora.nbc.gui.config.ConfigGui
import zone.nora.nbc.util.DelayedTask

class Command : CommandBase() {
    override fun getCommandName(): String = "nbc"

    override fun getCommandUsage(sender: ICommandSender?): String = "\u00a7c/nbc [refresh/config]"

    override fun getCommandAliases(): MutableList<String> = listOf("norasbetterchat", "chatwindows") as MutableList

    override fun processCommand(sender: ICommandSender?, args: Array<out String>) {
        if (args.isEmpty()) {
            Nbc.putChatMessage(getCommandUsage(sender))
        } else {
            when (args[0].toLowerCase()) {
                "r", "refresh" -> {
                    Nbc.refreshChatConfig()
                    NbcChatGui.window.clearChildren()
                    NbcChatGui.addWindows()
                    Nbc.putChatMessage("ok!")
                }
                "c", "config" -> DelayedTask(Runnable { Minecraft.getMinecraft().displayGuiScreen(ConfigGui()) }, 1)
                else -> Nbc.putChatMessage(getCommandUsage(sender))
            }
        }
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean = true
}