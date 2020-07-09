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

import club.sk1er.mods.core.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.util.IChatComponent
import org.apache.logging.log4j.LogManager
import zone.nora.nbc.gui.components.ChatWindowComponent
import zone.nora.nbc.util.TimedChatLine

class NbcGuiNewChat(mc: Minecraft) : GuiNewChat(mc) {
    override fun printChatMessage(p_146227_1_: IChatComponent?) {
        printChatMessageWithOptionalDeletion(p_146227_1_!!, 0)
    }

    override fun printChatMessageWithOptionalDeletion(chatComponent: IChatComponent, chatLineId: Int) {
        //if (BloodlustConfig.useNewChat) {
            //Bloodlust.log("[CHAT] ${chatComponent.formattedText}")
        LogManager.getLogger().info("[CHAT] ${chatComponent.formattedText}")
        val msg = ChatColor.stripColor(chatComponent.unformattedText)
        var addedToHideInAll = false
        NbcChatGui.window.childrenOfType<ChatWindowComponent>().forEach {
            val tabs = it.chatTabs
            tabs.forEach { tab ->
                var add = false
                val filters = tab.tabConfig.filter
                for (filter in filters) {
                    add = if (filter.startsWith("CF")) {
                        when (filter) {
                            "CF-A" -> false
                            "CF-C" -> msg.compareWithFormatted("Co-op >")
                            "CF-G" -> msg.compareWithFormatted("Guild >") || msg.compareWithFormatted("Discord >")
                            "CF-O" -> msg.compareWithFormatted("Officer >")
                            "CF-P" -> msg.compareWithFormatted("Party >")
                            else -> false
                        }
                    } else {
                        msg.startsWith(filter)
                    }
                    if (add) break
                }
                if (add) {
                    // intellij says this is always false but its wrong. DO NOT FIX -nora
                    if (tab.tabConfig.hideInAll) {
                        addedToHideInAll = true
                    }
                    tab.addChatMessage(TimedChatLine(System.currentTimeMillis(), chatComponent))
                }
            }
            if (!addedToHideInAll) {
                tabs.forEach { tab ->
                    if (tab.tabConfig.filter.contains("CF-A")) {
                        tab.addChatMessage(TimedChatLine(System.currentTimeMillis(), chatComponent))
                    }
                }
            }
        }
        //} else {
        //    super.printChatMessageWithOptionalDeletion(chatComponent, chatLineId)
        //}
    }

    private fun String.compareWithFormatted(formattedString: String): Boolean {
        val s = ChatColor.stripColor(formattedString.replace("&", "\u00a7"))
        return this.startsWith(s)
    }
}