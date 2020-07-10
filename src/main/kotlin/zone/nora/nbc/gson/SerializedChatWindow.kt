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

package zone.nora.nbc.gson

import com.google.gson.annotations.SerializedName

class SerializedChatWindow {
    @SerializedName("title")
    val title: String = "Unnamed Chat Window"

    @SerializedName("constraints")
    val constraints: SerializedChatWindowConstraints = SerializedChatWindowConstraints()

    @SerializedName("tabs")
    val tabs: ArrayList<SerializedChatTab> = ArrayList()

    override fun toString(): String = """{
        |   "title": "$title"
        |   "constraints": {
        |       "x": {
        |           "position": ${constraints.x.position},
        |           "align_opposite": ${constraints.x.alignOpposite}
        |       },
        |       "y": {
        |           "position": ${constraints.y.position},
        |           "align_opposite": ${constraints.y.alignOpposite}
        |       },
        |       "width": ${constraints.width},
        |       "height": ${constraints.height}
        |   },
        |   "tabs": [
        |       ${tabs.asString()}
        |   ]
        |}
    """.trimMargin()

    private fun ArrayList<SerializedChatTab>.asString(): String {
        var s = ""
        for (i in this.indices) {
            val tab = this[i]
            s += """{
                |   "title": "${tab.title}",
                |   "filter": ${tab.filter.joinToString(separator = ",\n", prefix = "[\n", postfix = "]")},
                |   "hide_in_all": ${tab.hideInAll},
                |   "prefix": "${tab.prefix}"
                |}
            """.trimMargin()
            if (i != this.lastIndex) s += ",\n"
        }
        return s
    }
}