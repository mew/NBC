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

class SerializedChatTab {
    @SerializedName("title")
    val title: String = "Unspecified"

    @SerializedName("filter")
    val filter: ArrayList<String> = ArrayList()

    @SerializedName("hide_in_all")
    val hideInAll: Boolean = false

    @SerializedName("prefix")
    val prefix: String = ""
}