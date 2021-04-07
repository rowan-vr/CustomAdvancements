package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Playtime extends AdvancementType {
	public Playtime() {
		super("playtime", Lang.ADVANCEMENT_TYPE_PLAYTIME_UNIT.getString());
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(CustomAdvancements.getInstance(), new Runnable() {
			@Override public void run() {
				for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
					progress(player, player.getUniqueId());
				}
			}
		}, 10L, 1200L);
	}

	@Override protected void onProgress(final Object e, final String value, final String path) {
		val player = (Player) e;
		progression(1, path, player.getUniqueId());
	}
}
