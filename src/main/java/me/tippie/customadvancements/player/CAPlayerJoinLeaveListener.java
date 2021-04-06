package me.tippie.customadvancements.player;

import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to player join and quit events and loads/unloads {@link me.tippie.customadvancements.player.CAPlayer}'s when joining/leaving
 */
public class CAPlayerJoinLeaveListener implements Listener {

	@EventHandler
	private void onJoin(final PlayerJoinEvent event) {
		if(CustomAdvancements.getCaPlayerManager().getPlayer(event.getPlayer().getUniqueId()) == null)CustomAdvancements.getCaPlayerManager().loadPlayer(event.getPlayer());
		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> CustomAdvancements.getCaPlayerManager().getPlayer(event.getPlayer().getUniqueId()).givePendingRewards(),25L);
	}

	@EventHandler
	private void onDisconnect(final PlayerQuitEvent event) {
		CustomAdvancements.getCaPlayerManager().savePlayer(event.getPlayer());
		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> {
			if (event.getPlayer().isOnline()) return;
			CustomAdvancements.getCaPlayerManager().unloadPlayer(event.getPlayer());
		},100L);
	}
}
