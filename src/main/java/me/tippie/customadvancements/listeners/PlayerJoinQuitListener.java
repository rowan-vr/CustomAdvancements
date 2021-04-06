package me.tippie.customadvancements.listeners;

import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to player join and quit events and loads/unloads {@link me.tippie.customadvancements.player.CAPlayer}'s when joining/leaving
 */
public class PlayerJoinQuitListener implements Listener {

	@EventHandler
	private void onJoin(final PlayerJoinEvent event) {
		CustomAdvancements.getCaPlayerManager().loadPlayer(event.getPlayer());
		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> CustomAdvancements.getCaPlayerManager().getPlayer(event.getPlayer().getUniqueId()).givePendingRewards(),40L);
	}

	@EventHandler
	private void onDisconnect(final PlayerQuitEvent event) {
		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> {
			CustomAdvancements.getCaPlayerManager().savePlayer(event.getPlayer());
			CustomAdvancements.getCaPlayerManager().unloadPlayer(event.getPlayer());
		},100L);
	}
}
