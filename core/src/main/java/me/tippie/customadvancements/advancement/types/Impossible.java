package me.tippie.customadvancements.advancement.types;

import lombok.AllArgsConstructor;
import me.tippie.customadvancements.advancement.AdvancementManager;
import org.bukkit.OfflinePlayer;

import java.util.AbstractMap;
import java.util.Map;

public class Impossible extends AdvancementType<Impossible.Progress> {

	private static Impossible registered;

	public Impossible() {
		super("impossible", "times");
		Impossible.registered = this;
	}

	public static void progress(OfflinePlayer player, int amount, String path){
		registered.progress(new Progress(player,amount,path), player.getUniqueId());
	}

	@Override protected void onProgress(Progress progress, String value, String path) {
		if (progress.path.equals(path)){
			progression(progress.amount, path, progress.player.getUniqueId());
		}
	}

	@AllArgsConstructor
	public static class Progress {
		private OfflinePlayer player;
		private int amount;
		private String path;
	}
}
