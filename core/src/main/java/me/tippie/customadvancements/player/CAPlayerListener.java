package me.tippie.customadvancements.player;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.PlayerOpenAdvancementTabEvent;
import me.tippie.customadvancements.advancement.requirement.types.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to player join and quit events and loads/unloads {@link me.tippie.customadvancements.player.CAPlayer}'s when joining/leaving
 */
public class CAPlayerListener implements Listener {

	@EventHandler
	private void onJoin(final PlayerJoinEvent event) {
		if (CustomAdvancements.getCaPlayerManager().getPlayer(event.getPlayer().getUniqueId()) == null)
			CustomAdvancements.getCaPlayerManager().loadPlayer(event.getPlayer());
		if (CustomAdvancements.getInternals() != null)
			CustomAdvancements.getInternals().registerAdvancementTabListener(event.getPlayer());

		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> {
			CAPlayer caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(event.getPlayer().getUniqueId());
			caPlayer.givePendingRewards();
			caPlayer.sendMinecraftGUI();
		}, 25L);

	}

	@EventHandler
	private void onDisconnect(final PlayerQuitEvent event) {
		CustomAdvancements.getCaPlayerManager().savePlayer(event.getPlayer());
		CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> {
			if (event.getPlayer().isOnline()) return;
			CustomAdvancements.getCaPlayerManager().unloadPlayer(event.getPlayer());
		}, 100L);
	}

	@EventHandler
	private void onLookingAtAdvancements(final PlayerOpenAdvancementTabEvent event) {
		if (CustomAdvancements.getInternals() != null && event.getTabId() != null) {
			try {
				AdvancementTree tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(event.getTabId().getKey().split("/")[0]);
				CustomAdvancements.getInternals().updateAdvancement(event.getPlayer(),tree.getAdvancements().toArray(new CAdvancement[]{}));
			} catch (InvalidAdvancementException ignored){}
		}
	}
}
