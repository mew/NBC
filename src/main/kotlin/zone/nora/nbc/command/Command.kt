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