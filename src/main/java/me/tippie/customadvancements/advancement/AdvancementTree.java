package me.tippie.customadvancements.advancement;

import lombok.Getter;
import lombok.val;
import lombok.var;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import org.bukkit.Bukkit;
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
			if (treeAdvancements == null) {
				data.createSection("advancements");
				data.save(config);
				treeAdvancements = data.getConfigurationSection("advancements");
			}
			assert treeAdvancements != null;
			for (final String advancementID : treeAdvancements.getKeys(false)) {
				String advancementType = treeAdvancements.getString(advancementID + ".type");
				int amount = treeAdvancements.getInt(advancementID + ".amount");
				String advancementLabel = treeAdvancements.getString(advancementID + ".label");
				if (advancementType == null) {
					advancementType = "empty";
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement '" + advancementID + "' of tree '" + label + "' did not have a type!");
				}
				if (amount == 0) {
					amount = 10;
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement '" + advancementID + "' of tree '" + label + "' did not have a amount!");
				}
				if (advancementLabel == null) {
					advancementLabel = "undefined" + advancementID;
					CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement '" + advancementID + "' of tree '" + label + "' did not have a label!");
				}
				final List<AdvancementReward> rewards = new ArrayList<>();
				if (treeAdvancements.getConfigurationSection(advancementID + ".rewards") != null) {
					val advancementRewardOptions = treeAdvancements.getConfigurationSection(advancementID + ".rewards");
					assert advancementRewardOptions != null;
					for (final String rewardID : advancementRewardOptions.getKeys(false)) {
						var type = advancementRewardOptions.getString(rewardID + ".type");
						val value = advancementRewardOptions.getString(rewardID + ".value");
						if (type == null) {
							type = "none";
							CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement reward '" + rewardID + "' of advancement '" + label + "." + advancementLabel + "' did not have a type!");
						}
						if (value == null) {
							type = "none";
							CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "Advancement reward '" + rewardID + "' of advancement  '" + label + "." + advancementLabel + "' did not have a value!");
						}
						rewards.add(new AdvancementReward(type, value));
					}
				}
				advancements.put(advancementLabel, new CAdvancement(advancementType, amount, advancementLabel, rewards));
			}
			if (treeOptions == null) {
				data.createSection("options");
				data.save(config);
				treeOptions = data.getConfigurationSection("options");
			}
			assert treeOptions != null;
			if (treeOptions.get("all_active") == null) treeOptions.set("all_active", false);
			val allActive = treeOptions.getBoolean("all_active");
			final List<AdvancementReward> treeRewards = new ArrayList<>();
			var rewardsOptions = data.getConfigurationSection("options.rewards");
			if (rewardsOptions == null) {
				data.createSection("options.rewards");
				data.save(config);
				rewardsOptions = data.getConfigurationSection("options.rewards");
			}
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
			this.options = new AdvancementTreeOptions(allActive, treeRewards);
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
		if (CustomAdvancements.getCaPlayerManager().getPlayer(uuid).amountCompleted(this.label) >= advancements.size()) {
			this.options.onComplete(Bukkit.getPlayer(uuid));
		}
	}
}
