package me.tippie.customadvancements.advancement;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import me.tippie.customadvancements.advancement.types.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public class AdvancementManager {

	private final Map<String, AdvancementType> advancementTypes = new HashMap<>();
	private final Map<String, AdvancementTree> advancementTrees = new HashMap<>();
	private final CustomAdvancements plugin;

	public AdvancementManager(final CustomAdvancements plugin) {
		this.plugin = plugin;
	}

	public void registerAdvancement(final AdvancementType advancementType) {
		CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Registering " + advancementType.getLabel() + " advancement type.");
		CustomAdvancements.getInstance().getServer().getPluginManager().registerEvents(advancementType, plugin);
		advancementTypes.put(advancementType.getLabel(), advancementType);
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
				advancementTrees.put(file.getName().split(".yml")[0], new AdvancementTree(file));
			}
		}
	}

	public void complete(final String path, final UUID playeruuid){
		val treeLabel = getAdvancementTreeLabel(path);
		val advancementLabel = getAdvancementLabel(path);
		getAdvancementTree(treeLabel).complete(advancementLabel,playeruuid);
	}

	public AdvancementType getAdvancementType(final String type) {
		return advancementTypes.values().stream().filter(advancement -> advancement.equals(type)).findAny().orElseGet(Empty::new);
	}

	public List<AdvancementTree> getAdvancementTrees() {
		return new ArrayList<>(advancementTrees.values());
	}

	public AdvancementTree getAdvancementTree(final String label) {
		return advancementTrees.get(label);
	}
	public List<AdvancementType> getAdvancementTypes() {
		return new ArrayList<>(advancementTypes.values());
	}

	public CAdvancement getAdvancement(final String path) {
		val treeLabel = getAdvancementTreeLabel(path);
		val advancementLabel = getAdvancementLabel(path);
		return advancementTrees.get(treeLabel).getAdvancement(advancementLabel);
	}

	public static String getAdvancementTreeLabel(final String path){
		return path.split("\\.")[0];
	}

	public static String getAdvancementLabel(final String path){
		return path.split("\\.")[1];
	}
}
