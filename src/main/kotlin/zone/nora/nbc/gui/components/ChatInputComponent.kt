package zone.nora.nbc.gui.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.utils.getStringSplitToWidth
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraftforge.client.ClientCommandHandler
import zone.nora.nbc.Nbc
import java.awt.Color

/**
 * UITextInput with minor tweaks.
 * @see club.sk1er.elementa.components.UITextInput
 */
open class ChatInputComponent @JvmOverloads constructor(
        chatWindowWidth: Float,
        private val placeholder: String = "",
        var wrapped: Boolean = true,
        var shadow: Boolean = true
) : UIComponent() {
    private val mc = Minecraft.getMinecraft()
    private val placeholderWidth = UniversalGraphicsHandler.getStringWidth(placeholder).toFloat()
    private val internalMouseClickAction = { mouseX: Float, mouseY: Float, button: Int ->
        if (active) {
            // TODO move cursor
        }
    }

    var text: String = ""
        set(value) {
            field = value
            textWidth = UniversalGraphicsHandler.getStringWidth(text).toFloat()
            updateAction(value)
        }
    var textWidth: Float = UniversalGraphicsHandler.getStringWidth(text).toFloat()
    var textOffset: Float = 0f

    var cursor: UIComponent = UIBlock(Color(255, 255, 255, 0))
    var cursorLocation = 0
    var active: Boolean = false
        set(value) {
            field = value
            if (value) animateCursor()
            else cursor.setColor(Color(255, 255, 255, 0).asConstraint())
        }

    var maxWidth: WidthConstraint = chatWindowWidth.pixels()
    var minWidth: WidthConstraint = chatWindowWidth.pixels()//UniversalGraphicsHandler.getStringWidth(placeholder).pixels()

    private var updateAction: (text: String) -> Unit = {}
    private var activateAction: (text: String) -> Unit = {}

    private var sentMessagesIndex = -1

    init {
        setHeight(9.pixels())

        alignCursor(if (text.isEmpty()) placeholder else text)

        onKeyType { typedChar, keyCode ->
            if (!active) return@onKeyType

            if (keyCode == 14) {
                // backspace
                if (text.isEmpty()) return@onKeyType
                text = text.substring(0, text.length - 1)
            } else if (keyCode == 203) {
                // left arrow
                if (cursorLocation > 0) cursorLocation--
            } else if (keyCode == 205) {
                // right arrow
                if (cursorLocation < text.length) cursorLocation++
            } else if (keyCode == 200) {
                // up arrow
                if ((sentMessagesIndex + 1) <= (mc.ingameGUI.chatGUI.sentMessages.size - 1)) {
                    sentMessagesIndex++
                    try {
                        text = mc.ingameGUI.chatGUI.sentMessages.asReversed()[sentMessagesIndex]
                    } catch (_: Exception) { }
                }
            } else if (keyCode == 208) {
                if (sentMessagesIndex - 1 >= -1) {
                    sentMessagesIndex--
                    text = try { mc.ingameGUI.chatGUI.sentMessages.asReversed()[sentMessagesIndex]
                    } catch (_: Exception) { "" }
                }
            } else if (keyCode == 15) {
                Nbc.putChatMessage("Tab completion is coming soon\u2122!!")
                // TODO add tab completion.
            } else if (keyCode == 28 || keyCode == 156) {
                // 255
                if (text.length < 256) {
                    mc.ingameGUI.chatGUI.addToSentMessages(text)
                    if (text.startsWith("/") && ClientCommandHandler.instance.executeCommand(mc.thePlayer, text) != 0)
                        return@onKeyType
                    if (Nbc.colourChatMod) {
                        mc.thePlayer.sendChatMessage(text)
                    } else {
                        val c01PacketChatMessage = C01PacketChatMessage(text)
                        if (text.length > 100) {
                            try {
                                val field = c01PacketChatMessage::class.java.getDeclaredField("message")
                                field.isAccessible = true
                                field.set(c01PacketChatMessage, text)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(c01PacketChatMessage)
                    }
                    sentMessagesIndex = -1
                    mc.displayGuiScreen(null)
                    val chatWindow = (parent.parent) as ChatWindowComponent
                    text = chatWindow.chatTabs[chatWindow.activeTab].tabConfig.prefix
                }
            } else if (
                    keyCode in 2..13 ||
                    keyCode in 16..27 ||
                    keyCode in 30..41 ||
                    keyCode in 43..53 ||
                    keyCode in 71..83 ||
                    keyCode in 145..147 ||
                    keyCode == 55 ||
                    keyCode == 181 ||
                    keyCode == 57
            ) {
                // normal key input
                if (text.length < 255) {
                    if (GuiScreen.isKeyComboCtrlV(keyCode)) {
                        // very unoptimal but idc lol.
                        val clipboard = GuiScreen.getClipboardString()
                        if (text.length + clipboard.length < 255) {
                            text += clipboard
                        } else {
                            for (char in clipboard.toCharArray()) {
                                if (text.length + 1 >= 255) break
                                text += char
                            }
                        }
                    } else {
                        text += typedChar
                        cursorLocation++
                    }
                }
            }
        }

        cursor.constrain {
            x = (textWidth + 1).pixels()
            y = (0).pixels()
            width = 1.pixels()
            height = 8.pixels()
        } childOf this
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int) {
        if (isHovered()) internalMouseClickAction(mouseX - getLeft(), mouseY - getTop(), button)
        super.mouseClick(mouseX, mouseY, button)
    }

    override fun draw() {
        beforeDraw()

        val y = getTop()
        val color = getColor()

        UniversalGraphicsHandler.enableBlend()

        UniversalGraphicsHandler.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)

        val displayText = if (text.isEmpty() && !this.active) placeholder else text

        if (wrapped) {
            val lines = alignCursor(displayText)
            val numLines = lines.size
            if (numLines > 0) {
                val heightNumLines = 10 * numLines
                val chatWindow = parent.parent as ChatWindowComponent
                val cwHeight = chatWindow.windowConfig.constraints.height - 10 + heightNumLines
                if (chatWindow.getHeight() != cwHeight) {
                    chatWindow.setHeight(((chatWindow.windowConfig.constraints.height - 10) + heightNumLines).pixels())
                    chatWindow.chatContainer.setHeight(FillConstraint() - heightNumLines.pixels())
                    chatWindow.visibleTimestampHolder.setHeight(FillConstraint() - heightNumLines.pixels())
                    (chatWindow.chatInputBox).animate {
                        setHeightAnimation(Animations.LINEAR, 0.2f, heightNumLines.pixels())
                    }
                }
            }
            lines.forEachIndexed { index, line ->
                UniversalGraphicsHandler.drawString(line, getLeft(), y + index * 9, color.rgb, shadow)
            }
        } else {
            alignCursor()
            UniversalGraphicsHandler.drawString(displayText, getLeft() + textOffset, y, color.rgb, shadow)
        }

        UniversalGraphicsHandler.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)


        super.draw()
    }

    /**
     * Callback to run whenever the text in the input changes,
     * i.e. every time a valid key is pressed.
     */
    fun onUpdate(action: (text: String) -> Unit) = apply {
        updateAction = action
    }

    /**
     * Callback to run when the user hits the Return key, thus
     * "activating" the input.
     */
    fun onActivate(action: (text: String) -> Unit) = apply {
        activateAction = action
    }

    private fun alignCursor(displayText: String = ""): List<String> {
        val width = if (text.isEmpty() && !this.active) placeholderWidth else textWidth
        setWidth(width.pixels().minMax(minWidth, maxWidth))

        if (wrapped) {
            val lines = getStringSplitToWidth(
                    displayText,
                    getWidth() / getTextScale()
            )

            cursor.setX((UniversalGraphicsHandler.getStringWidth(lines.last()) + 1).pixels())
            cursor.setY(((lines.size - 1) * 9).pixels())
            setHeight((lines.size * 9).pixels())
            return lines
        } else {
            cursor.setX(width.pixels())
            textOffset = if (active) {
                if (width > getWidth()) {
                    cursor.setX(0.pixels(true))
                    getWidth() - width - 1
                } else 0f
            } else 0f
        }

        return emptyList()
    }

    private fun animateCursor() {
        if (!active) return
        cursor.animate {
            setColorAnimation(Animations.OUT_CIRCULAR, 0.5f, Color.WHITE.asConstraint())
            onComplete {
                if (!active) return@onComplete
                cursor.animate {
                    setColorAnimation(Animations.IN_CIRCULAR, 0.5f, Color(255, 255, 255, 0).asConstraint())
                    onComplete {
                        if (active) animateCursor()
                    }
                }
            }
        }
    }
}