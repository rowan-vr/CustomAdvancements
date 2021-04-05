package me.tippie.customadvancements.advancement;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.types.AdvancementRequirementType;
import me.tippie.customadvancements.advancement.reward.types.AdvancementRewardType;
import me.tippie.customadvancements.advancement.reward.types.None;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import me.tippie.customadvancements.advancement.types.Empty;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

/**
 * This class keeps track of all advacementTrees and -Types
 */
public class AdvancementManager {

	/**
	 * Map with key type label and value the {@link AdvancementType} belonging to it.
	 */
	private final Map<String, AdvancementType> advancementTypes = new HashMap<>();
	/**
	 * Map with key tree label and value the {@link AdvancementTree} belonging to it.
	 */
	private final Map<String, AdvancementTree> advancementTrees = new HashMap<>();
	/**
	 * Map with key tree label and value the {@link AdvancementRewardType} belonging to it.
	 */
	private final Map<String, AdvancementRewardType> advancementRewardTypes = new HashMap<>();
	/**
	 * Map with key tree label and value the {@link AdvancementRewardType} belonging to it.
	 */
	private final Map<String, AdvancementRequirementType> advancementRequirementTypes = new HashMap<>();


	/**
	 * Makes new {@link AdvancementManager}
	 */
	public AdvancementManager() {
	}

	/**
	 * Registers an advancement type with the plugin, example usage:
	 * {@code advancementManager.registerAdvancement(new ExampleType());}
	 *
	 * @param advancementType the instance of an advancement type
	 */
	public void registerAdvancement(final AdvancementType advancementType) {
		CustomAdvancements.getInstance().getLogger().log(Level.INFO, "Registering " + advancementType.getLabel() + " advancement type.");
		CustomAdvancements.getInstance().getServer().getPluginManager().registerEvents(advancementType, CustomAdvancements.getInstance());
		advancementTypes.put(advancementType.getLabel(), advancementType);
	}

	/**
	 * Registers an advancement reward type with the plugin, example usage:
	 * {@code advancementManager.registerAdvancementReward(new ExampleType());}
	 *
	 * @param advancementRewardType the instance of an advancement type
	 */
	public void registerAdvancementReward(final AdvancementRewardType advancementRewardType) {
		advancementRewardTypes.put(advancementRewardType.getLabel(), advancementRewardType);
	}


	/**
	 * Registers an advancement requirement type with the plugin, example usage:
	 * {@code advancementManager.registerAdvancementRequirement(new ExampleType());}
	 *
	 * @param advancementRequirementType the instance of an advancement type
	 */
	public void registerAdvancementRequirement(final AdvancementRequirementType advancementRequirementType) {
		advancementRequirementTypes.put(advancementRequirementType.getLabel(), advancementRequirementType);
	}

	/**
	 * Loads the advancement trees and puts them into {@link AdvancementManager#advancementTrees}
	 */
	public void loadAdvancements() {
		final Path advancementFolder = Paths.get(CustomAdvancements.getInstance().getDataFolder() + "/advancements");
		if (!Files.exists(advancementFolder)) {
			try {
				Files.createDirectories(advancementFolder);
			} catch (final IOException ex) {
				CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.");
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

	/**
	 * Executes the complete complete actions for an advancement and marks it as completed
	 *
	 * @param path       The path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @param playeruuid UUID of the minecraft player
	 */
	public void complete(final String path, final UUID playeruuid) throws InvalidAdvancementException {
		val treeLabel = getAdvancementTreeLabel(path);
		val advancementLabel = getAdvancementLabel(path);
		getAdvancementTree(treeLabel).complete(advancementLabel,playeruuid);
	}

	/**
	 * Searches for the advancement type using the label it is registered with.
	 * @param type the label of an advancement type
	 * @return the {@link AdvancementType}
	 */
	public AdvancementType getAdvancementType(final String type) {
		return advancementTypes.values().stream().filter(advancement -> advancement.equals(type)).findAny().orElseGet(Empty::new);
	}

	/**
	 * Converts map {@link AdvancementManager#advancementTrees} into a list and returns it.
	 * @return list of all registered {@link AdvancementTree}'s
	 */
	public List<AdvancementTree> getAdvancementTrees() {
		return new ArrayList<>(advancementTrees.values());
	}

	/**
	 * Searches {@link AdvancementManager#advancementTrees} for the tree matching the label
	 *
	 * @param label the unique label of an {@link AdvancementTree}
	 * @return the {@link AdvancementTree} of the label given as input
	 */
	public AdvancementTree getAdvancementTree(final String label) throws InvalidAdvancementException {
		if (advancementTrees.get(label) == null) throw new InvalidAdvancementException();
		return advancementTrees.get(label);
	}

	/**
	 * Converts map {@link AdvancementManager#advancementTypes} into a list and returns it.
	 * @return list of all registered {@link AdvancementType}'s
	 */
	public List<AdvancementType> getAdvancementTypes() {
		return new ArrayList<>(advancementTypes.values());
	}

	/**
	 * Gets the correct advancement from the correct tree registered.
	 *
	 * @param path The path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return the {@link CAdvancement} belonging to that path
	 */
	public CAdvancement getAdvancement(final String path) throws InvalidAdvancementException {
		val treeLabel = getAdvancementTreeLabel(path);
		val advancementLabel = getAdvancementLabel(path);
		return advancementTrees.get(treeLabel).getAdvancement(advancementLabel);
	}

	/**
	 * Gets the tree label from the given path
	 * @param path The path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return the tree label of the given path
	 */
	public static String getAdvancementTreeLabel(final String path) {
		return path.split("\\.")[0];
	}

	/**
	 * Gets the advancement label from the given path
	 *
	 * @param path The path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return the advancement label of the given path
	 */
	public static String getAdvancementLabel(final String path) {
		return path.split("\\.")[1];
	}

	/**
	 * Searches for the advancement type using the label it is registered with.
	 *
	 * @param type the label of an advancement type
	 * @return the {@link AdvancementType}
	 */
	public AdvancementRewardType getAdvancementRewardType(final String type) {
		return advancementRewardTypes.values().stream().filter(advancement -> advancement.equals(type)).findAny().orElseGet(None::new);
	}

	/**
	 * Gets the type of an requirement using the label
	 *
	 * @param type label of an registered {@link AdvancementRequirementType}
	 * @return the {@link AdvancementRequirementType} that belongs to the given label
	 */
	public AdvancementRequirementType getAdvancementRequirementType(final String type) {
		return advancementRequirementTypes.values().stream().filter(advancement -> advancement.equals(type)).findAny().orElseGet(me.tippie.customadvancements.advancement.requirement.types.None::new);
	}

	/**
	 * Unregisters all listeners, {@link AdvancementType}'s, {@link AdvancementTree}'s, {@link AdvancementRewardType}'s, {@link AdvancementRequirementType}'s
	 */
	public void unregisterAll() {
		HandlerList.unregisterAll();
		advancementTypes.clear();
		advancementTrees.clear();
		advancementRewardTypes.clear();
		advancementRequirementTypes.clear();
	}
}
