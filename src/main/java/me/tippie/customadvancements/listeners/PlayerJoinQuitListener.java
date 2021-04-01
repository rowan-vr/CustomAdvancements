package me.tippie.customadvancements.listeners;

import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

/**
 * Listens to player join and quit events and loads/unloads {@link me.tippie.customadvancements.player.CAPlayer}'s when joining/leaving
 */
public class PlayerJoinQuitListener implements Listener {

	@EventHandler
	private void onJoin(final PlayerJoinEvent event) {
		CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Attempting to load player: " + event.getPlayer().getName());
		CustomAdvancements.getCaPlayerManager().loadPlayer(event.getPlayer());
	}

	@EventHandler
	private void onDisconnect(final PlayerQuitEvent event) {
		CustomAdvancements.getCaPlayerManager().savePlayer(event.getPlayer());
		CustomAdvancements.getCaPlayerManager().unloadPlayer(event.getPlayer());
	}
}
