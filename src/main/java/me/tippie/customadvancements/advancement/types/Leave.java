package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave extends AdvancementType {

	public Leave() {
		super("leave", Lang.ADVANCEMENT_TYPE_LEAVE_UNIT.getString());
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, final String value, final String path) {
		final PlayerQuitEvent playerQuitEvent = (PlayerQuitEvent) event;
		progression(1, path, playerQuitEvent.getPlayer().getUniqueId());
	}
}
