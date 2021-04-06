package me.tippie.customadvancements.player;

import lombok.Getter;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the player manager of {@link CAPlayer}'s
 */
public class CAPlayerManager {
	/**
	 * Map of UUID and their {@link CAPlayer}
	 */
	@Getter private final Map<UUID, CAPlayer> caPlayers = new HashMap<>();

	/**
	 * Loads a player and their data
	 *
	 * @param player {@link Player} that must be loaded
	 * @see AdvancementProgressFile#loadFile()
	 */
	public void loadPlayer(final Player player) {
		caPlayers.put(player.getUniqueId(), new CAPlayer(player.getUniqueId()));
	}

	/**
	 * Saves the data from a player
	 *
	 * @param player {@link Player} that must be saved
	 * @see AdvancementProgressFile#safeFile()
	 */
	public void savePlayer(final Player player) {
		caPlayers.get(player.getUniqueId()).getAdvancementProgressFile().safeFile();
		caPlayers.get(player.getUniqueId()).savePendingRewards();
	}

	/**
	 * Saves the data from a player
	 *
	 * @param playeruuid the uuid of the player that must be saved
	 * @see AdvancementProgressFile#safeFile()
	 */
	public void savePlayer(final UUID playeruuid) {
		caPlayers.get(playeruuid).getAdvancementProgressFile().safeFile();
	}

	/**
	 * Gets a {@link CAPlayer} with their UUID if loaded
	 *
	 * @param uuid uuid of a player
	 * @return {@link CAPlayer} belonging to this UUID
	 */
	public CAPlayer getPlayer(final UUID uuid) {
		return caPlayers.get(uuid);
	}

	/**
	 * Unloads a player
	 *
	 * @param player {@link Player} that must be unloaded
	 */
	public void unloadPlayer(final Player player) {
		caPlayers.remove(player.getUniqueId());
	}
}
