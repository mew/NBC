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

import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels

class ConfigToggleButtonComponent(private val buttonText: String, var value: Boolean) : ConfigOptionButtonComponent(buttonText) {
    init {
        constrain {
            x = CenterConstraint()
            y = SiblingConstraint(3f)
            height = 15.pixels()
            width = RelativeConstraint(.9f)
        }

        btnText.setText(getButtonText())
        onMouseClick {
            value = !value
            btnText.setText(getButtonText())
        }
    }

    private fun getButtonText(): String = "$buttonText: ${if (value) "\u00a7atrue" else "\u00a7cfalse"}"
}