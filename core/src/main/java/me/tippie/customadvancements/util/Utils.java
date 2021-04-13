package me.tippie.customadvancements.util;

import com.google.gson.JsonObject;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

public class Utils {

    // Format for JSON component:
    // https://minecraft.fandom.com/wiki/Advancement/JSON_format
    public static void showToast(Player player, String title, String description){
        JsonObject advancementJSON = new JsonObject();

        JsonObject display = new JsonObject();
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", false);
        display.addProperty("hidden",true);
        display.addProperty("frame", "task");
        display.addProperty("title", title);
        display.addProperty("description", description);

        JsonObject icon = new JsonObject();
        icon.addProperty("item", "minecraft:stone");

        display.add("icon", icon);


        advancementJSON.add("display",display);
        NamespacedKey key = new NamespacedKey(CustomAdvancements.getInstance(), "showtoast"+Math.round(Math.random()*10000));
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, String.valueOf(advancementJSON));


        Bukkit.getUnsafe().removeAdvancement(key);
    }
}
