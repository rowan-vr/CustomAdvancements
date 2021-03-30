package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class AdvancementTree {
	private @Getter
	final List<CAdvancement> advancements = new ArrayList<>();
	private @Getter
	final String label;

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
				advancements.add(new CAdvancement(questType, amount, questLabel));
				CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Loaded advancement tree " + config.getName());
			}
		} catch (Exception ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
		}
	}
}
