package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join extends AdvancementType {
	public Join() {
		super("join", Lang.ADVANCEMENT_TYPE_JOIN_UNIT.getString());
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, final String value, final String path) {
		final PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) event;
		progression(1, path, playerJoinEvent.getPlayer().getUniqueId());
	}
}
