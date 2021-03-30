package me.tippie.customadvancements.advancement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdvancementTree {
	private final List<Advancement> advancements = new ArrayList<>();

	AdvancementTree(final File config) {
		final FileConfiguration data = YamlConfiguration.loadConfiguration(config);
		final Set<String> tree = data.getKeys(false);
		for (final String questID : tree) {
			final String questType = data.getString(questID + ".type");
			final int amount = data.getInt(questID + ".amount");
			advancements.add(new Advancement(questType, amount));
		}
	}
}
