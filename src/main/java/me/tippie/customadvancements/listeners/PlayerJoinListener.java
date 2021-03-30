package me.tippie.customadvancements.listeners;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.player.CAPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void PlayerJoinEvent(final PlayerJoinEvent event) {
		CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Attempting to load player: " +event.getPlayer().getName());
		CAPlayerManager.loadPlayer(event.getPlayer());
	}
}
