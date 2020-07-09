package zone.nora.nbc.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import zone.nora.nbc.Nbc
import zone.nora.nbc.gui.chat.NbcChatGui
import zone.nora.nbc.gui.components.ChatWindowComponent

class EventListener {
    @SubscribeEvent
    fun onWorldLoad(e: WorldEvent.Load) {
        Minecraft.getMinecraft().ingameGUI = Nbc.inGameGui
        NbcChatGui.addWindows()
    }

    @SubscribeEvent
    fun onRenderTick(e: TickEvent.RenderTickEvent) {
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen !is GuiChat) return
        if (e.phase != TickEvent.Phase.END) return
        try { NbcChatGui.window.draw() } catch (_: Exception) { }
    }

    @SubscribeEvent
    fun onChatKeys(e: InputEvent) {
        val mc = Minecraft.getMinecraft()
        if (mc.gameSettings.keyBindChat.isKeyDown) {
            mc.displayGuiScreen(NbcChatGui())
        } else if (mc.gameSettings.keyBindCommand.isKeyDown) {
            NbcChatGui.window.childrenOfType<ChatWindowComponent>().forEach {
                if (it.chatInput.active) if (it.chatInput.text.isBlank()) it.chatInput.text = "/"
            }
            mc.displayGuiScreen(NbcChatGui())
        }
    }
}