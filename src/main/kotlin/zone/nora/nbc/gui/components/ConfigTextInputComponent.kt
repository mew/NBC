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
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.effect
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.effects.ScissorEffect
import java.awt.Color

class ConfigTextInputComponent(
    w: Float,
    placeholder: String = "",
    restrictions: TextInputComponent.Restrictions = TextInputComponent.Restrictions.NONE
) : UIBlock(Color(0, 0, 0)) {
    val textInput = TextInputComponent(w.pixels(), placeholder, false, restrictions = restrictions)

    init {
        constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 10.pixels()
            width = w.pixels()
        }

        textInput.constrain {
            x = 0.pixels()
            y = 0.pixels()
            height = FillConstraint()
            width = FillConstraint()
        } childOf this

        textInput.text = placeholder

        onMouseClick { e ->
            parent.childrenOfType<ConfigTextInputComponent>().forEach {
                it.textInput.active = false
            }
            textInput.active = true
            e.stopPropagation()
        }

        this effect ScissorEffect()
    }
}