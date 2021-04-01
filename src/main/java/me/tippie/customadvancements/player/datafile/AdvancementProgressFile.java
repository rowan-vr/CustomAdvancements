package me.tippie.customadvancements.player.datafile;

import lombok.Getter;
import lombok.var;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents the progress file of a player
 */
public class AdvancementProgressFile {
	/**
	 * The uuid of the player this progress file belogns to
	 */
	@Getter private final UUID playeruuid;

	/**
	 * Creates a new {@link AdvancementProgressFile} but does not load nor create it if it doesn't exist
	 *
	 * @param playeruuid uuid of the player this progress file belongs to
	 */
	public AdvancementProgressFile(final UUID playeruuid) {
		this.playeruuid = playeruuid;
	}

	/**
	 * Loads the player's data file from '%plugindir%/data/ and returns the map of it.
	 *
	 * @return a map with key the string of the path (see {@link me.tippie.customadvancements.advancement.AdvancementManager#getAdvancementTreeLabel(String)} for documentation about the path) of the advancement and value the {@link me.tippie.customadvancements.player.datafile.AdvancementProgress} that belongs to it.
	 */
	public Map<String, AdvancementProgress> loadFile() {
		final Path dataFolder = Paths.get(
				CustomAdvancements.getInstance().getDataFolder() + "/data");
		if (!Files.exists(dataFolder)) {
			try {
				Files.createDirectories(dataFolder);
			} catch (final IOException ex) {
				CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
			}
		}
		final File file = new File(dataFolder.toString() + "/" + this.playeruuid.toString() + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException ex) {
				CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
			}
		}
		final var result = new HashMap<String, AdvancementProgress>();
		try {
			final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
			data.load(file);
			final List<AdvancementTree> advancementTrees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
			for (final AdvancementTree advancementTree : advancementTrees) {
				final List<CAdvancement> advancements = advancementTree.getAdvancements();
				for (final CAdvancement advancement : advancements) {
					if (data.get(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "progress") == null)
						data.set(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "progress", 0);
					final int progress = data.getInt(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "progress");
					if (data.get(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "active") == null)
						data.set(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "active", false);
					final boolean active = data.getBoolean(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "active");
					if (data.get(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "completed") == null)
						data.set(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "completed", false);
					final boolean completed = data.getBoolean(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "completed", false);
					result.put(advancementTree.getLabel() + "." + advancement.getLabel(), AdvancementProgress.advancementProgressFromFile(progress, active, completed));
				}
			}
			data.save(file);
		} catch (final Exception ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
		}
		return result;
	}

	/**
	 * Saves the progress file of this player
	 */
	public void safeFile() {
		final File file = new File(CustomAdvancements.getInstance().getDataFolder() + "/data/" + this.playeruuid.toString() + ".yml");
		final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		try {
			for (final Map.Entry<String, AdvancementProgress> entry : CustomAdvancements.getCaPlayerManager().getPlayer(this.playeruuid).getAdvancementProgress().entrySet()) {
				data.set(entry.getKey() + ".progress", entry.getValue().getProgress());
				data.set(entry.getKey() + ".active", entry.getValue().isActive());
				data.set(entry.getKey() + ".completed", entry.getValue().isCompleted());
			}
			data.save(file);
		} catch (final IOException ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
		}
	}
}
