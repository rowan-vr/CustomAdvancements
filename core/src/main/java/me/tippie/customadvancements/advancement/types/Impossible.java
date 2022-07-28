package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.advancement.AdvancementManager;
import org.bukkit.OfflinePlayer;

import java.util.AbstractMap;
import java.util.Map;

public class Impossible extends AdvancementType<Map.Entry<OfflinePlayer,Integer>> {

	private static Impossible registered;

	public Impossible() {
		super("impossible", "times");
		Impossible.registered = this;
	}

	public static void progress(OfflinePlayer player, int amount){
		registered.progress(new AbstractMap.SimpleEntry<>(player,amount), player.getUniqueId());
	}

	@Override protected void onProgress(Map.Entry<OfflinePlayer, Integer> entry, String value, String path) {
		progression(entry.getValue(),path,entry.getKey().getUniqueId());
	}
}
