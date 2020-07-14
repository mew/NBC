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

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.utils.getStringSplitToWidth
import zone.nora.nbc.gson.SerializedChatWindow
import zone.nora.nbc.gui.chat.NbcChatGui
import zone.nora.nbc.util.DelayedTask
import zone.nora.nbc.util.TimedChatLine
import java.awt.Color

class ChatWindowComponent(val windowConfig: SerializedChatWindow) : UIBlock(Color(0, 0, 0, 100)) {
    var bar: UIComponent
    var chatInput: ChatInputComponent
    var visibleTimestampHolder: UIBlock
    var timestampHolder: ChatScrollerComponent
    var chatContainer: UIContainer
    var scroller: ChatScrollerComponent
    var tabHolder: UIBlock
    var activeTab: Int = 0
    var chatInputBox: UIBlock
    val chatTabs = ArrayList<ChatTabComponent>()

    private var isDragging = false
    private var dragOffset = 0f to 0f

    init {
        constrain {
            val constraints = windowConfig.constraints
            x = constraints.x.position.pixels(constraints.x.alignOpposite)
            y = constraints.y.position.pixels(constraints.y.alignOpposite)

            height = constraints.height.pixels()
            width = constraints.width.pixels()
        }

        bar = UIBlock(Color(0, 0, 0, 200)).constrain {
            x = 0.pixels()
            y = 0.pixels()

            width = FillConstraint()
            height = 20.pixels()
        }.onMouseClick { e ->
            isDragging = true
            dragOffset = e.absoluteX to e.absoluteY
        }.onMouseRelease {
            isDragging = false
        }.onMouseDrag { mouseX, mouseY, _ ->
            if (!isDragging) return@onMouseDrag

            val absoluteX = mouseX + getLeft()
            val absoluteY = mouseY + getTop()

            val deltaX = absoluteX - dragOffset.first
            val deltaY = absoluteY - dragOffset.second

            dragOffset = absoluteX to absoluteY

            val newX = this@ChatWindowComponent.getLeft() + deltaX
            val newY = this@ChatWindowComponent.getTop() + deltaY

            this@ChatWindowComponent.setX(newX.pixels())
            this@ChatWindowComponent.setY(newY.pixels())
        } childOf this

        UIText(windowConfig.title).constrain {
            x = 1.pixels()
            y = 1.pixels()

            color = Color.WHITE.asConstraint()
        } childOf bar

        tabHolder = UIBlock(Color(0, 0, 0, 0)).constrain {
            x = 0.pixels()
            y = 10.pixels()
            height = FillConstraint()
            width = FillConstraint() - 1.pixels()
        } childOf bar

        windowConfig.tabs.forEach {
            val tab = ChatTabComponent(it) childOf tabHolder
            chatTabs.add(tab)
        }

        visibleTimestampHolder = UIBlock(Color(0, 0, 0, 200)).constrain {
            x = 0.pixels()
            y = SiblingConstraint()

            width = "| 00:00:00".width().pixels()
            height = FillConstraint() - 10.pixels()
        } childOf this //effect ScissorEffect()

        timestampHolder = ChatScrollerComponent(scrollOpposite = true, allowPhysicalScrolling = false).constrain {
            height = FillConstraint()
            width = FillConstraint()
        } childOf visibleTimestampHolder

        UIBlock(windowConfig.colour.getColour()).constrain {
            x = 0.pixels()
            y = 20.pixels()

            height = FillConstraint() - 10.pixels()
            width = 2.pixels()
        } childOf this

        UIBlock(Color(0, 0, 0, 200)).constrain {
            x = this@ChatWindowComponent.getWidth().pixels() - 2.pixels()
            y = 20.pixels()

            width = 2.pixels()
            height = FillConstraint() - 10.pixels()
        } childOf this

        //chatContainer = UIContainer().constrain {
        //    x = "| 00:00:00".width().pixels()
        //    y = 20.pixels()
        //
        //    width = FillConstraint() - 2.pixels()
        //    height = FillConstraint() - 10.pixels()
        //} childOf this //effect ScissorEffect()

        chatInputBox = UIBlock(Color(0, 0, 0, 200)).constrain {
            x = 0.pixels()
            y = (this@ChatWindowComponent.getHeight() - 10).pixels()

            height = 10.pixels()
            width = FillConstraint()
        } childOf this

        chatInputBox effect ScissorEffect()

        chatInput = (ChatInputComponent(windowConfig.constraints.width).constrain {
            x = 1.pixels()
            y = 1.pixels()

            width = FillConstraint()
            height = FillConstraint()
        }.onMouseClick { event ->
            NbcChatGui.window.childrenOfType<ChatWindowComponent>().forEach {
                it.chatInput.active = false
            }
            (this as ChatInputComponent).active = true
            event.stopPropagation()
        } as ChatInputComponent) childOf chatInputBox

        chatContainer = UIContainer().constrain {
            x = "| 00:00:00".width().pixels()
            y = 20.pixels()
            width = FillConstraint() - 2.pixels()
            height = FillConstraint() - 10.pixels()
        } childOf this //effect ScissorEffect()

        scroller = ChatScrollerComponent(scrollOpposite = true).constrain {
            width = FillConstraint()
            height = FillConstraint()
        }.onMouseScroll { delta ->
            timestampHolder.triggerScroll(delta)
            (this as ChatScrollerComponent).triggerScroll(delta)
        } as ChatScrollerComponent childOf chatContainer

        onMouseClick { event ->
            parent.removeChild(this)
            parent.addChild(this)
            NbcChatGui.window.childrenOfType<ChatWindowComponent>().forEach {
                it.chatInput.active = false
            }
            event.stopPropagation()
        }

        DelayedTask(Runnable { chatTabs[activeTab].setActive() }, 1)
    }

    fun addChatMessageToCurrentTab(chatLine: TimedChatLine) {
        val chatMessageComponent = ChatMessageComponent(chatLine.chatComponent)

        scroller.insertChild(chatMessageComponent, 0)
        for (i in getStringSplitToWidth(chatLine.chatComponent.formattedText, scroller.getWidth()).indices) {
            timestampHolder.insertChild(UIText(if (i == 0) "\u00a77${chatLine.getTimeFormatted()}" else " ").constrain {
                x = "||".width().pixels()
                y = SiblingConstraint(alignOpposite = true)
            }, 0)
        }
    }

    fun updateChat() {
        val tabChatHistory = chatTabs[activeTab].chatHistory
        tabChatHistory.reverse()
        chatContainer.removeChild(scroller)
        scroller = ChatScrollerComponent().constrain {
            width = RelativeConstraint(1f)
            height = RelativeConstraint(1f)
        }.onMouseScroll { delta->
            timestampHolder.triggerScroll(delta)
            (this as ChatScrollerComponent).triggerScroll(delta)
        } as ChatScrollerComponent childOf chatContainer
        timestampHolder.actualHolder.clearChildren()

        if (chatInput.text.split(" ").size < 3) chatInput.text = chatTabs[activeTab].tabConfig.prefix

        tabChatHistory.forEach {
            scroller.addChild(ChatMessageComponent(it.chatComponent))
            for (i in getStringSplitToWidth(it.chatComponent.formattedText, scroller.getWidth()).indices) {
                timestampHolder.addChild(UIText(if (i == 0) "\u00a77${it.getTimeFormatted()}" else " ").constrain {
                    x = "||".width().pixels()
                    y = SiblingConstraint(alignOpposite = true)
                })
            }
        }
        tabChatHistory.reverse()
    }

    fun open() {
        // TODO hide input field until GuiChat is opened.
    }
}