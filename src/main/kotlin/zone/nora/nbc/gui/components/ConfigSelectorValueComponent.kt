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

import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.animate
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.elementa.dsl.constrain
import java.awt.Color

class ConfigSelectorValueComponent(title: String, val id: Int) : UIText(title) {
    var selected = false

    init {
        constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
        }

        onMouseEnter {
            if (!selected) {
                animate {
                    setColorAnimation(Animations.LINEAR, .2f, Color(150, 150, 0).asConstraint())
                }
            }
        }

        onMouseLeave {
            if (!selected) {
                animate {
                    setColorAnimation(Animations.LINEAR, .2f, Color(255, 255, 255).asConstraint())
                }
            }
        }

        onMouseClick {
            parent.childrenOfType<ConfigSelectorValueComponent>().forEach {
                it.deselect()
            }
            setColor(Color(255, 255, 0).asConstraint())
            selected = true
        }
    }

    private fun deselect() {
        setColor(Color(255, 255, 255).asConstraint())
        selected = false
    }
}