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

package zone.nora.nbc.gui.components

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ResourceLocation
import zone.nora.nbc.gson.SerializedChatTab
import zone.nora.nbc.util.TimedChatLine
import java.awt.Color

class ChatTabComponent(val tabConfig: SerializedChatTab) : UIBlock(Color(0, 0, 0, 0)) {
    val visibleTab: UIBlock
    val notificationBar: UIBlock
    var unread = false
    var active = false
    val chatHistory = ArrayList<TimedChatLine>()
    private val activeColour = Color(39, 39, 39, 255)

    init {
        constrain {
            x = SiblingConstraint()
            y = 0.pixels()

            height = FillConstraint()
            width = tabConfig.title.width().pixels() + 6.pixels()
        }

        visibleTab = UIBlock(Color(39, 39, 39, 0)).constrain {
            x = 1.pixels()
            y = 0.pixels()

            height = FillConstraint()
            width = RelativeConstraint(1f) - 1.pixels()
        } childOf this

        UIText(tabConfig.title).constrain {
            x = CenterConstraint()
            y = CenterConstraint() - 2.pixels()
        } childOf visibleTab

        notificationBar = UIBlock(Color.GRAY).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(1f)

            height = 1.pixels()
            width = tabConfig.title.width().pixels()
        } childOf visibleTab

        onMouseEnter {
            if (!active) {
                visibleTab.animate {
                    setColorAnimation(Animations.LINEAR, 0.2f, activeColour.asConstraint())
                }
            }
        }

        onMouseLeave {
            if (!active) {
                visibleTab.animate {
                    setColorAnimation(Animations.LINEAR, 0.2f, Color(39, 39, 39, 0).asConstraint())
                }
            }
        }

        onMouseClick { event ->
            event.stopPropagation()
            Minecraft.getMinecraft().soundHandler.playSound(
                PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F)
            )
            if (!active) setActive()
        }

        // so scroll component does freak out and not render chat input
        chatHistory.add(TimedChatLine(System.currentTimeMillis(), ChatComponentText("")))
    }

    fun setActive() {
        val chatWindow = parent.parent.parent as ChatWindowComponent
        chatWindow.chatTabs.forEach {
            it.setInactive()
        }
        active = true
        unread = false
        visibleTab.setColor(activeColour.asConstraint())
        notificationBar.animate {
            setColorAnimation(Animations.LINEAR, 0.2f, Color.GRAY.asConstraint())
        }
        chatWindow.activeTab = chatWindow.chatTabs.indexOf(this)
        chatWindow.updateChat()
    }

    private fun setInactive() {
        //if (active) notificationBar.setColor(Color.GRAY.asConstraint())
        active = false
        visibleTab.setColor(Color(39, 39, 39, 0).asConstraint())
    }

    fun addChatMessage(chat: TimedChatLine) {
        chatHistory.add(chat)
        if (!active) {
            notificationBar.animate {
                setColorAnimation(Animations.LINEAR, 0.2f, Color(200, 200, 0).asConstraint())
            }
            unread = true
        } else {
            (parent.parent.parent as ChatWindowComponent).addChatMessageToCurrentTab(chat)
        }
    }
}