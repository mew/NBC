package zone.nora.nbc.gson

import com.google.gson.annotations.SerializedName
import java.awt.Color

class SerializedColour {
    @SerializedName("red")
    var red: Int = 200

    @SerializedName("green")
    var green: Int = 200

    @SerializedName("blue")
    var blue: Int = 0

    fun getColour(): Color = Color(red, green, blue)
}