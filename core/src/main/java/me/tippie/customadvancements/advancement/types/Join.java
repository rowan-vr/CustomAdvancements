package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join extends AdvancementType<PlayerJoinEvent> {
	public Join() {
		super("join", Lang.ADVANCEMENT_TYPE_JOIN_UNIT.getString());
	}

	@EventHandler(ignoreCancelled = true)
	public void onJoin(final PlayerJoinEvent event) {
		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> progress(event, event.getPlayer().getUniqueId()), 20L);
	}

	@Override protected void onProgress(final PlayerJoinEvent event, final String value, final String path) {
		progression(1, path, event.getPlayer().getUniqueId());
	}
}
