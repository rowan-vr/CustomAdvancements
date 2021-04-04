package me.tippie.customadvancements.advancement;

import lombok.Getter;
import lombok.val;
import lombok.var;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

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
	 * Contains all the options of this {@link AdvancementTree}
	 */
	@Getter private AdvancementTreeOptions options;

	/**
	 * The label of this {@link AdvancementTree}
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
			var treeAdvancements = data.getConfigurationSection("advancements");
			var treeOptions = data.getConfigurationSection("options");

			//Initialize advancements
			if (treeAdvancements == null) {
				data.createSection("advancements");
				data.save(config);
				treeAdvancements = data.getConfigurationSection("advancements");
			}
			assert treeAdvancements != null;
			for (final String advancementLabel : treeAdvancements.getKeys(false)) {
				String advancementType = treeAdvancements.getString(advancementLabel + ".type");
				final String advancementValue = treeAdvancements.getString(advancementLabel + ".value");
				int amount = treeAdvancements.getInt(advancementLabel + ".amount");
				if (advancementType == null) {
					advancementType = "empty";
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement '" + advancementLabel + "' of tree '" + label + "' did not have a type!");
				}
				if (advancementValue == null) {
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement '" + advancementLabel + "' of tree '" + label + "' did not have a value!");
				}
				if (amount == 0) {
					amount = 10;
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement '" + advancementLabel + "' of tree '" + label + "' did not have a amount!");
				}

				//Initialize advancement rewards
				final List<AdvancementReward> rewards = new ArrayList<>();
				if (treeAdvancements.getConfigurationSection(advancementLabel + ".rewards") != null) {
					val advancementRewardOptions = treeAdvancements.getConfigurationSection(advancementLabel + ".rewards");
					assert advancementRewardOptions != null;
					for (final String rewardLabel : advancementRewardOptions.getKeys(false)) {
						var type = advancementRewardOptions.getString(rewardLabel + ".type");
						val value = advancementRewardOptions.getString(rewardLabel + ".value");
						if (type == null) {
							type = "none";
							CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement reward '" + rewardLabel + "' of advancement '" + label + "." + advancementLabel + "' did not have a type!");
						}
						if (value == null) {
							type = "none";
							CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement reward '" + rewardLabel + "' of advancement  '" + label + "." + advancementLabel + "' did not have a value!");
						}
						rewards.add(new AdvancementReward(type, value));
					}
				}

				//Initialize advancement requirements
				final List<AdvancementRequirement> requirements = new ArrayList<>();
				if (treeAdvancements.getConfigurationSection(advancementLabel + ".requirements") != null) {
					val advancementRewardOptions = treeAdvancements.getConfigurationSection(advancementLabel + ".requirements");
					assert advancementRewardOptions != null;
					for (final String requirementLabel : advancementRewardOptions.getKeys(false)) {
						var type = advancementRewardOptions.getString(requirementLabel + ".type");
						val value = advancementRewardOptions.getString(requirementLabel + ".value");
						if (type == null) {
							type = "none";
							CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement requirement '" + requirementLabel + "' of advancement '" + label + "." + advancementLabel + "' did not have a type!");
						}
						if (value == null) {
							type = "none";
							CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement requirement '" + requirementLabel + "' of advancement  '" + label + "." + advancementLabel + "' did not have a value!");
						}
						requirements.add(new AdvancementRequirement(type, value));
					}
				}

				//Initialize advancement display options
				if (treeAdvancements.getConfigurationSection(advancementLabel + ".display") == null) {
					treeAdvancements.createSection(advancementLabel + ".display");
				}
				val displayOptions = treeAdvancements.getConfigurationSection(advancementLabel + ".display");
				assert displayOptions != null;
				if (displayOptions.get("name") == null) {
					displayOptions.set("name", advancementLabel);
					data.save(config);
				}
				val displayName = displayOptions.getString("name");
				val displayDescription = displayOptions.getString("description");

				if (displayOptions.get("gui_location") == null) {
					displayOptions.set("gui_location", "auto");
					data.save(config);
				}
				val guiLocation = displayOptions.getString("gui_location");

				//Initialize advancement display item
				if (displayOptions.getString("item") == null) {
					displayOptions.set("item", "CHEST");
					data.save(config);
				}

				val itemString = displayOptions.getString("item");
				var itemMaterial = (itemString != null) ? Material.getMaterial(itemString) : Material.BARRIER;
				if (itemMaterial == null) itemMaterial = Material.BARRIER;
				val displayItem = new ItemStack(itemMaterial);

				if (displayOptions.getString("unit") == null) {
					val type = CustomAdvancements.getAdvancementManager().getAdvancementType(advancementType);
					displayOptions.set("unit", (type != null) ? type.getDefaultUnit() : null);
					data.save(config);
				}
				val displayUnit = displayOptions.getString("unit");

				advancements.put(advancementLabel, new CAdvancement(advancementType, advancementValue, amount, advancementLabel, this.label, rewards, requirements, displayName, displayDescription, displayItem, guiLocation, displayUnit));
			}

			//Initialize options
			if (treeOptions == null) {
				data.createSection("options");
				data.save(config);
				treeOptions = data.getConfigurationSection("options");
			}
			assert treeOptions != null;

			if (treeOptions.get("auto_active") == null) treeOptions.set("auto_active", false);
			val autoActive = treeOptions.getBoolean("auto_active");

			if (treeOptions.get("gui_location") == null) {
				treeOptions.set("gui_location", "auto");
				data.save(config);
				CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "AdvancementTree '" + label + "' did not have a gui location! Automatically set it to 'auto'");
			}
			val guiLocation = treeOptions.getString("gui_location");

			if (treeOptions.get("display_name") == null) {
				treeOptions.set("display_name", label);
				data.save(config);
				CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "AdvancementTree '" + label + "' did not have a gui location! Automatically set the tree label as display name.");
			}
			val displayName = treeOptions.getString("display_name");

			val description = treeOptions.getString("description");


			final List<AdvancementReward> treeRewards = new ArrayList<>();
			var rewardsOptions = data.getConfigurationSection("options.rewards");
			if (rewardsOptions == null) {
				data.createSection("options.rewards");
				data.save(config);
				rewardsOptions = data.getConfigurationSection("options.rewards");
			}

			//Initialize tree rewards
			assert rewardsOptions != null;
			for (final String rewardID : rewardsOptions.getKeys(false)) {
				var type = rewardsOptions.getString(rewardID + ".type");
				val value = rewardsOptions.getString(rewardID + ".value");
				if (type == null) {
					type = "none";
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "AdvancementTree reward '" + rewardID + "' of tree '" + label + "' did not have a type!");
				}
				if (value == null) {
					type = "none";
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "AdvancementTree reward '" + rewardID + "' of tree '" + label + "' did not have a value!");
				}
				treeRewards.add(new AdvancementReward(type, value));
			}

			//Initializing tree display item
			if (treeOptions.getString("item") == null) {
				treeOptions.set("item", "OAK_SAPLING");
				data.save(config);
			}

			val itemString = treeOptions.getString("item");
			var itemMaterial = (itemString != null) ? Material.getMaterial(itemString) : Material.BARRIER;
			if (itemMaterial == null) itemMaterial = Material.BARRIER;
			val displayItem = new ItemStack(itemMaterial);

			//Finishing up
			this.options = new AdvancementTreeOptions(autoActive, guiLocation, treeRewards, displayName, description, displayItem);
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
	public CAdvancement getAdvancement(final String label) throws InvalidAdvancementException {
		if (advancements.get(label) == null) throw new InvalidAdvancementException();
		return advancements.get(label);
	}

	/**
	 * Executes the complete actions of this tree
	 *
	 * @param completedLabel label of completed advancement
	 * @param uuid           uuid of an player
	 */
	public void complete(final String completedLabel, final UUID uuid) throws InvalidAdvancementException {
		if (advancements.get(completedLabel) == null) throw new InvalidAdvancementException();
		advancements.get(completedLabel).complete(uuid);
		if (CustomAdvancements.getCaPlayerManager().getPlayer(uuid).amountCompleted(this.label) >= advancements.size()) {
			this.options.onComplete(Bukkit.getPlayer(uuid));
		}
	}
}
