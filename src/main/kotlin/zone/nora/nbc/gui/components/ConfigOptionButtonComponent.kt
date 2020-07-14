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
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import java.awt.Color

open class ConfigOptionButtonComponent(buttonText: String, var enabled: Boolean = true, highlightColour: Color = Color(255, 255, 0)) : UIBlock(Color(0, 0, 0)) {
    val actualButton: UIBlock
    val btnText: UIText

    init {
        actualButton = UIBlock(getColour()).constrain {
            x = 1.pixels()
            y = 1.pixels()
            width = RelativeConstraint(1f) - 2.pixels()
            height = RelativeConstraint(1f) - 2.pixels()
        } childOf this

        btnText = UIText(buttonText).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = .8f.pixels()
        } childOf actualButton

        onMouseEnter {
            if (enabled) {
                actualButton.animate {
                    setColorAnimation(Animations.LINEAR, .2f, Color(180, 180, 180).asConstraint())
                }
                btnText.animate {
                    setColorAnimation(Animations.LINEAR, .2f, highlightColour.asConstraint())
                }
            }
        }

        onMouseLeave {
            if (enabled) {
                actualButton.animate {
                    setColorAnimation(Animations.LINEAR, .2f, Color(140, 140, 140).asConstraint())
                }
                btnText.animate {
                    setColorAnimation(Animations.LINEAR, .2f, Color(255, 255, 255).asConstraint())
                }
            }
        }

        onMouseClick { e ->
            e.stopPropagation()
            Minecraft.getMinecraft().soundHandler.playSound(
                PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F)
            )
        }
    }

    fun changeState(enable: Boolean) {
        enabled = enable
        actualButton.setColor(getColour().asConstraint())
    }

    private fun getColour(): Color = if (enabled) Color(140, 140, 140) else Color(80, 80, 80)
}