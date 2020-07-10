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
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.minus
import club.sk1er.elementa.dsl.pixels
import java.awt.Color

class ConfigWindowComponent(title: String) : UIBlock(Color(20, 20, 20)) {
    val bar: WindowHeaderComponent = WindowHeaderComponent(title) childOf this
    val body: UIBlock = UIBlock(Color(50, 50, 50)).constrain {
        x = 1.pixels()
        y = SiblingConstraint(1f)
        width = FillConstraint() - 1.pixels()
        height = FillConstraint() - 1.pixels()
    } childOf this
}