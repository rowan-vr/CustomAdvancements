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

        //Creating an impossible critirion to grant it using the plugin
        JsonObject criteria = new JsonObject();
        JsonObject crit = new JsonObject();

        crit.addProperty("trigger","minecraft:bee_nest_destroyed");
        JsonObject conditions = new JsonObject();
        conditions.addProperty("block", "minecraft:bedrock");
        JsonObject item = new JsonObject();
        item.addProperty("item", "minecraft:air");
        conditions.add("item", item);
        conditions.addProperty("num_bees_inside", 100);
        criteria.add("crit",crit);
        advancementJSON.add("criteria", criteria);


        advancementJSON.add("display",display);
        NamespacedKey key = new NamespacedKey(CustomAdvancements.getInstance(), "showtoast"+Math.round(Math.random()*10000));
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, String.valueOf(advancementJSON));
        for (String critirion : player.getAdvancementProgress(advancement).getRemainingCriteria()){
            player.getAdvancementProgress(advancement).awardCriteria(critirion);
        }

        Bukkit.getUnsafe().removeAdvancement(key);
    }
}
