package me.tippie.customadvancements.player.datafile;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementManager;
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

public class AdvancementProgressFile {

	private @Getter
	final Map<String, AdvancementProgress> advancementProgress = new HashMap();
	private final CustomAdvancements plugin = CustomAdvancements.getInstance();
	private final UUID playeruuid;

	public AdvancementProgressFile(final UUID playeruuid) {
		this.playeruuid = playeruuid;
	}

	public void loadFile() {
		final UUID uuid = this.playeruuid;
		final Path dataFolder = Paths.get(plugin.getDataFolder() + "/data");
		if (!Files.exists(dataFolder)) {
			try {
				Files.createDirectories(dataFolder);
			} catch (final IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
			}
		}
		final File file = new File(dataFolder.toString() + "/" + uuid.toString() + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
			}
		}
		try {
			final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
			data.load(file);
			final List<AdvancementTree> advancementTrees = AdvancementManager.getAdvancementTrees();
			for (final AdvancementTree advancementTree : advancementTrees) {
				final List<CAdvancement> advancements = advancementTree.getAdvancements();
				for (final CAdvancement advancement : advancements) {
					if (data.get(advancementTree.getLabel() + "." + advancement.getLabel()) == null) {
						data.set(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "progress", 0);
						data.set(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "started", false);
						data.set(advancementTree.getLabel() + "." + advancement.getLabel() + "." + "completed", false);
					} //TODO add loading of data.
					//TODO fix: advancementProgress.put(advancementTree.getLabel() + "." + advancement.getLabel(), (AdvancementProgress) data.get(advancementTree.getLabel() + "." + advancement.getLabel()));
				}
			}
			data.save(file);
		} catch (final Exception ex) {
			plugin.getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
		}
	}
}
