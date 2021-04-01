package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class AdvancementTree {
	final Map<String, CAdvancement> advancements = new HashMap<>();
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
				advancements.put(questLabel, new CAdvancement(questType, amount, questLabel));
				CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Loaded advancement tree " + config.getName());
			}
		} catch (final Exception ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
		}
	}

	public List<CAdvancement> getAdvancements() {
		return new ArrayList<>(advancements.values());
	}

	public CAdvancement getAdvancement(final String label) {
		return advancements.get(label);
	}

	public void complete(final String completedLabel, final UUID uuid) {
		advancements.get(completedLabel).complete(uuid, label);
	}
}
