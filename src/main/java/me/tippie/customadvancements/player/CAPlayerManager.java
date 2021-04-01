package me.tippie.customadvancements.player;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CAPlayerManager {
	@Getter
	private final Map<UUID, CAPlayer> caPlayers = new HashMap<>();

	public void loadPlayer(final Player player) {
		caPlayers.put(player.getUniqueId(), new CAPlayer(player.getUniqueId()));
	}

	public void savePlayer(final Player player) {
		caPlayers.get(player.getUniqueId()).getAdvancementProgressFile().safeFile();
	}

	public CAPlayer getPlayer(final UUID uuid) {
		return caPlayers.get(uuid);
	}

	public void unloadPlayer(final Player player) {
		caPlayers.remove(player.getUniqueId());
	}
}
