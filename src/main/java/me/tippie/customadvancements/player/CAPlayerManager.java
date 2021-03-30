package me.tippie.customadvancements.player;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CAPlayerManager {
	private static final Map<UUID, CAPlayer> caPlayers = new ConcurrentHashMap<>();

   public static void loadPlayer(final Player player) {
		caPlayers.put(player.getUniqueId(), new CAPlayer(player.getUniqueId()));
	}
}
