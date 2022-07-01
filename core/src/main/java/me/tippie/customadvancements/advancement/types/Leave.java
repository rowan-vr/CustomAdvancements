package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave extends AdvancementType<PlayerQuitEvent> {

	public Leave() {
		super("leave", Lang.ADVANCEMENT_TYPE_LEAVE_UNIT.getString());
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final PlayerQuitEvent event, final String value, final String path) {
		progression(1, path, event.getPlayer().getUniqueId());
	}
}
