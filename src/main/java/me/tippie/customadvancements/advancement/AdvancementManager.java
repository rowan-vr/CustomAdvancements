package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import me.tippie.customadvancements.advancement.types.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class AdvancementManager {
	private @Getter
	static final List<AdvancementType> advancementsTypes = new ArrayList<>();
	private @Getter
	static final List<AdvancementTree> advancementTrees = new LinkedList<>();
	private final CustomAdvancements plugin;

	public AdvancementManager(final CustomAdvancements plugin) {
		this.plugin = plugin;
	}

	public void registerAdvancement(final AdvancementType advancement) {
		CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Registering " + advancement.getLabel() + " advancement type.");
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

	public static AdvancementType getAdvancementType(final String type) {
		return advancementsTypes.stream().filter(advancement -> advancement.equals(type)).findAny().orElseGet(Empty::new);
	}

}
