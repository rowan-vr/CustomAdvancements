package me.tippie.customadvancements.player;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the player manager of {@link CAPlayer}'s
 */
public class CAPlayerManager {
	/**
	 * Map of UUID and their {@link CAPlayer}
	 */
	@Getter private final Map<UUID, CAPlayer> caPlayers = new HashMap<>();
	private final Map<UUID, CAPlayer> cachedCaPlayers = new HashMap<>();

	/**
	 * Loads a player and their data
	 *
	 * @param player {@link Player} that must be loaded
	 * @see AdvancementProgressFile#loadFile()
	 */
	public void loadPlayer(final Player player) {
		if (!caPlayers.containsKey(player.getUniqueId())) {
			caPlayers.put(player.getUniqueId(), new CAPlayer(player.getUniqueId()));
		}
	}

	/**
	 * Saves the data from a player
	 *
	 * @param player {@link Player} that must be saved
	 * @see AdvancementProgressFile#saveFile()
	 */
	public void savePlayer(final Player player) {
		savePlayer(player.getUniqueId());
	}

	/**
	 * Saves the data from a player
	 *
	 * @param playeruuid the uuid of the player that must be saved
	 * @see AdvancementProgressFile#saveFile()
	 */
	public void savePlayer(final UUID playeruuid) {
		getPlayer(playeruuid).save();
	}

	/**
	 * Gets a {@link CAPlayer} with their UUID if loaded
	 *
	 * @param uuid uuid of a player
	 * @return {@link CAPlayer} belonging to this UUID
	 */
	public CAPlayer getPlayer(final UUID uuid) {
		CAPlayer player = caPlayers.get(uuid);
		if (player == null) {
			player = cachedCaPlayers.get(uuid);
		}
		return player;
	}

	public CompletableFuture<CAPlayer> getOfflinePlayer(final UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			CAPlayer player = caPlayers.get(uuid);
			if (player != null) {
				return player;
			}
			player = cachedCaPlayers.get(uuid);
			if (player != null) {
				return player;
			}

			player = new CAPlayer(uuid);
			cachedCaPlayers.put(uuid, player);
			Bukkit.getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> cachedCaPlayers.remove(uuid), 20*60L);

			return player;
		});
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
