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

package zone.nora.nbc

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import zone.nora.nbc.command.Command
import zone.nora.nbc.gson.SerializedChatWindow
import zone.nora.nbc.gui.NbcGuiInGame
import zone.nora.nbc.util.EventListener
import java.io.File

@Mod(modid = "NBC", name = "Nora's Better Chat", version = "1.0", modLanguage = "kotlin")
class Nbc {
    @EventHandler
    fun init(e: FMLInitializationEvent) {
        val directory = File(configDirectory())
        if (!directory.exists()) directory.mkdirs()

        configuration = refreshChatConfig()
        inGameGui = NbcGuiInGame(Minecraft.getMinecraft())
        Minecraft.getMinecraft().ingameGUI = inGameGui

        MinecraftForge.EVENT_BUS.register(EventListener())

        ClientCommandHandler.instance.registerCommand(Command())
    }

    companion object {
        lateinit var inGameGui: NbcGuiInGame
        var configuration: ArrayList<SerializedChatWindow> = ArrayList()

        fun configDirectory(): String = "${Minecraft.getMinecraft().mcDataDir}/NBC/"

        fun refreshChatConfig(): ArrayList<SerializedChatWindow> {
            val chatConfigFile = File("${configDirectory()}ChatWindows.json")
            val chatConfigJson = if (chatConfigFile.exists()) {
                chatConfigFile.readText()
            } else {
                chatConfigFile.createNewFile()
                chatConfigFile.writeText(DEFAULT_CONFIGURATION)
                DEFAULT_CONFIGURATION
            }
            val jsonArray: JsonArray = try {
                JsonParser().parse(chatConfigJson).asJsonArray
            } catch (_: Exception) {
                JsonParser().parse(DEFAULT_CONFIGURATION).asJsonArray
            }
            return Gson().fromJson(jsonArray, typeToken<ArrayList<SerializedChatWindow>>())
        }

        fun putChatMessage(str: String) =
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("[\u00a76NBC\u00a7f] $str"))

        private inline fun <reified T> typeToken() = object: TypeToken<T>() {}.type

        private const val DEFAULT_CONFIGURATION = "[\n" +
            "  {\n" +
            "    \"title\": \"Primary Chat\",\n" +
            "    \"constraints\": {\n" +
            "      \"x\": {\n" +
            "        \"position\": 0\n" +
            "      },\n" +
            "      \"y\": {\n" +
            "        \"position\": 0,\n" +
            "        \"align_opposite\": true\n" +
            "      },\n" +
            "      \"width\": 350,\n" +
            "      \"height\": 200\n" +
            "    },\n" +
            "    \"tabs\": [\n" +
            "      {\n" +
            "        \"title\": \"All\",\n" +
            "        \"filter\": [\n" +
            "          \"CF-A\"\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Guild\",\n" +
            "        \"filter\": [\n" +
            "          \"CF-G\"\n" +
            "        ],\n" +
            "        \"hide_in_all\": true,\n" +
            "        \"prefix\": \"/gc \"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Party\",\n" +
            "        \"filter\": [\n" +
            "          \"CF-P\"\n" +
            "        ],\n" +
            "        \"hide_in_all\": true,\n" +
            "        \"prefix\": \"/pc \"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Co-op\",\n" +
            "        \"filter\": [\n" +
            "          \"CF-C\"\n" +
            "        ],\n" +
            "        \"hide_in_all\": true,\n" +
            "        \"prefix\": \"/cc \"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"title\": \"Secondary Chat\",\n" +
            "    \"constraints\": {\n" +
            "      \"x\": {\n" +
            "        \"position\": 10,\n" +
            "        \"align_opposite\": true\n" +
            "      },\n" +
            "      \"y\": {\n" +
            "        \"position\": 5,\n" +
            "        \"align_opposite\": true\n" +
            "      },\n" +
            "      \"width\": 250,\n" +
            "      \"height\": 200\n" +
            "    },\n" +
            "    \"tabs\": [\n" +
            "      {\n" +
            "        \"title\": \"DMs\",\n" +
            "        \"filter\": [\n" +
            "          \"From \",\n" +
            "          \"To \"\n" +
            "        ],\n" +
            "        \"hide_in_all\": true,\n" +
            "        \"prefix\": \"/w \"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]"
    }
}
