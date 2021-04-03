package me.tippie.customadvancements.advancement;

import lombok.Getter;
import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents an advancement in this plugin
 */
public class CAdvancement {
	/**
	 * The {@link AdvancementType} of this advancement.
	 */
	@Getter private final AdvancementType type;

	/**
	 * The progress required to complete this advancement
	 */
	@Getter private final int maxProgress;

	/**
	 * The value of the type, e.g. what block needs to be broken for {@link me.tippie.customadvancements.advancement.types.BlockBreak}
	 */
	@Getter private final String value;

	/**
	 * The label of this advancement
	 */
	@Getter private final String label;

	/**
	 * The tree this advancement belongs to
	 */
	@Getter private final String tree;
	/**
	 * The rewards when completing this advancement
	 */
	@Getter private final List<AdvancementReward> rewards;

	/**
	 * The requirements for this advancement to activate this advancement
	 */
	private final List<AdvancementRequirement> requirements;

	/**
	 * Creates a new {@link CAdvancement}
	 *
	 * @param type        String of the type of this advancement
	 * @param maxProgress integer of the progress required to complete this advancement
	 * @param label       String of the label of this advancement
	 */
	CAdvancement(final String type, final String value, final int maxProgress, final String label, final String tree, final List<AdvancementReward> rewards, final List<AdvancementRequirement> requirements) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementType(type);
		this.value = value;
		this.maxProgress = maxProgress;
		this.label = label;
		this.rewards = rewards;
		this.tree = tree;
		this.requirements = requirements;
	}

	/**
	 * Executes the completed actions for this advancement
	 *
	 * @param uuid UUID of player who completed the advancement
	 */
	public void complete(final UUID uuid) {
		val player = Bukkit.getPlayer(uuid);
		assert player != null;
		for (final AdvancementReward reward : rewards) {
			reward.onComplete(player);
		}
	}

	/**
	 * Checks if all requirements of an advancement are met for a player
	 *
	 * @param player {@link Player} which this should be checked on
	 * @return true or false if this player meets the requirements
	 */
	public boolean meetRequirements(final Player player) {
		for (final AdvancementRequirement requirement : requirements) {
			if (!requirement.isMet(player)) return false;
		}
		return true;
	}

	/**
	 * Gets the path of this advancement
	 *
	 * @return the path of an advancement formatted as 'treeLabel.advancementLabel'
	 */
	public String getPath() {
		return this.tree + "." + this.label;
	}

	/**
	 * The list of all requirements of this quest
	 *
	 * @return list with all {@link AdvancementRequirement}'s for this quest
	 */
	public List<AdvancementRequirement> getRequirements() {
		return requirements;
	}

	/**
	 * Gets the list of all requirements met or not met for a specific player
	 *
	 * @param isMet  return the list of met or not met requirements
	 * @param player the player that should be checked on if the requirements are met
	 * @return list with all met or not met {@link AdvancementRequirement}'s for a specific player
	 */
	public List<AdvancementRequirement> getRequirements(final boolean isMet, final Player player) {
		return requirements.stream().filter(requirement -> isMet == requirement.isMet(player)).collect(Collectors.toList());
	}
}
