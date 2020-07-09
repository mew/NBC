package zone.nora.nbc.gui

import club.sk1er.mods.core.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.util.IChatComponent
import org.apache.logging.log4j.LogManager
import zone.nora.nbc.gui.components.ChatWindowComponent
import zone.nora.nbc.util.TimedChatLine

/*
 * Created by Nora Cos on 22/05/20.
 * Last modified 24/05/20.
 */
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