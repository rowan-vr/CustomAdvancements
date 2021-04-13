package me.tippie.customadvancements.util;

import com.google.gson.JsonObject;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

public class Utils {
    public static void showToast(Player player){
        JsonObject advancementJSON = new JsonObject();
        JsonObject display = new JsonObject();
        display.addProperty("show_toast", true);
        advancementJSON.add("display",display);
        NamespacedKey key = new NamespacedKey(CustomAdvancements.getInstance(), "showtoast"+Math.round(Math.random()*10000));
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, String.valueOf(advancementJSON));
    }
}
