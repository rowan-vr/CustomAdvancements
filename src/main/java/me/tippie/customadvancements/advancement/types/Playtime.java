package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Playtime extends AdvancementType {
	public Playtime() {
		super("playtime", Lang.ADVANCEMENT_TYPE_PLAYTIME_UNIT.getString());
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(CustomAdvancements.getInstance(), () -> {
			for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
				progress(player, player.getUniqueId());
			}
		}, 10L, 1200L);
	}

	@Override protected void onProgress(final Object e, String value, final String path) {
		val player = (Player) e;
		if (value == null || value.equalsIgnoreCase("any") || value.equalsIgnoreCase("")) {
			progression(1, path, player.getUniqueId());
		} else {
			val worldName = player.getWorld().getName();
			boolean not = false;
			if (value.startsWith("!")) {
				value = value.toLowerCase().substring(1);
				not = true;
			}
			List<String> worlds = new ArrayList<>(Arrays.asList(value.split(",")));
			if ((worlds.contains(worldName) && !not) || (!worlds.contains(worldName) && not))
				progression(1, path, player.getUniqueId());
		}
	}
}
