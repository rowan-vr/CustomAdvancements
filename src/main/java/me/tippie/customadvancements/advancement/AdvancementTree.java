package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents an advancement tree loaded from a configuration.
 */
public class AdvancementTree {

	/**
	 * The map with key the label of an {@link CAdvancement}
	 */
	final Map<String, CAdvancement> advancements = new HashMap<>();

	/**
	 * The label of the {@link AdvancementTree}
	 *
	 * @return the label of this {@link AdvancementTree}
	 */
	@Getter private final String label;

	/**
	 * Creates a new {@link AdvancementTree} out of the given file
	 *
	 * @param config file that has the configuration for an {@link AdvancementTree}
	 */
	AdvancementTree(final File config) {
		label = config.getName().split(".yml")[0];
		CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Attempting to load advancement tree " + config.getName());
		try {
			final FileConfiguration data = YamlConfiguration.loadConfiguration(config);
			data.load(config);
			final Set<String> tree = data.getKeys(false);
			for (final String questID : tree) {
				final String questType = data.getString(questID + ".type");
				final int amount = data.getInt(questID + ".amount");
				final String questLabel = data.getString(questID + ".label");
				advancements.put(questLabel, new CAdvancement(questType, amount, questLabel));
			}
			CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Loaded advancement tree " + config.getName());
		} catch (final Exception ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
		}
	}

	/**
	 * Converts {@link AdvancementTree#advancements} to a list of {@link CAdvancement}'s
	 *
	 * @return list of all the {@link CAdvancement}'s of this {@link AdvancementTree}
	 */
	public List<CAdvancement> getAdvancements() {
		return new ArrayList<>(advancements.values());
	}

	/**
	 * Finds the {@link CAdvancement} with the given label in this tree.
	 *
	 * @param label the label of an {@link CAdvancement} in this tree
	 * @return the {@link CAdvancement}
	 */
	public CAdvancement getAdvancement(final String label) {
		return advancements.get(label);
	}

	/**
	 * Executes the complete actions of this tree
	 *
	 * @param completedLabel label of completed advancement
	 * @param uuid           uuid of an player
	 */
	public void complete(final String completedLabel, final UUID uuid) {
		advancements.get(completedLabel).complete(uuid, label);
	}
}
