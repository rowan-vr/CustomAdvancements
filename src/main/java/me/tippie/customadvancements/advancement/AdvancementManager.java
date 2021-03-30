package me.tippie.customadvancements.advancement;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import me.tippie.customadvancements.advancement.types.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

public class AdvancementManager {
	private static List<AdvancementType> advancementsTypes;
	private static List<AdvancementTree> advancementTrees;
	private final CustomAdvancements plugin;

	AdvancementManager(final CustomAdvancements plugin) {
		this.plugin = plugin;
	}

	public static void registerAdvancement(final AdvancementType advancement) {
		advancementsTypes.add(advancement);
	}

	public void loadAdvancements() {
		final Path advancementFolder = Paths.get(plugin.getDataFolder() + "/advancements");
		if (!Files.exists(advancementFolder)) {
			try {
				Files.createDirectories(advancementFolder);
			} catch (final IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
			}
		}
		final File dir = new File(advancementFolder.toString());
		final File[] advancementDirectoryContent = dir.listFiles();
		assert advancementDirectoryContent != null;
		for (final File file : advancementDirectoryContent) {
			if (file.getName().endsWith(".yml")) {
				advancementTrees.add(new AdvancementTree(file));
			}
		}
	}

	public AdvancementType getAdvancementType(String type) {
		return advancementsTypes.stream().filter(advancement -> advancement.equals(type)).findAny().orElse(new Empty());
	}
}
