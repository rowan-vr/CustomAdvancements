package me.tippie.customadvancements.advancement.reward.types;

import lombok.Getter;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import org.bukkit.entity.Player;

public abstract class AdvancementRewardType {
	/**
	 * The label of this {@link AdvancementRewardType}
	 */
	@Getter private final String label;

	/**
	 * Creates a new reward type
	 *
	 * @param label the label of the reward
	 */
	AdvancementRewardType(final String label) {
		this.label = label;
	}

	/**
	 * Executes the reward actions
	 *
	 * @param value  value of the {@link AdvancementReward}
	 * @param player {@link Player} this reward should be executed for
	 */
	public abstract void onReward(String value, Player player);

	/**
	 * Compares a string with an advancement reward type
	 *
	 * @param in String that should be compared with
	 * @return true if the string is equals to the label of this reward type, false otherwise
	 */
	public boolean equals(final String in) {
		return this.label.equalsIgnoreCase(in);
	}
}
