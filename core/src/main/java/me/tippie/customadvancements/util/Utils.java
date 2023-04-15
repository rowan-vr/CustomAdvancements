package me.tippie.customadvancements.util;

import com.google.gson.JsonObject;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class Utils {
    private final static Pattern NAMESPACED_KEY_PATTERN = Pattern.compile("^[a-z0-9/._-]+$");

    public static boolean validateNamespacedKey(String str) {
        return NAMESPACED_KEY_PATTERN.matcher(str).matches();
    }
}
