package me.tippie.customadvancements.advancement.reward.types;

import org.bukkit.entity.Player;

/**
 * Represents an invalid advancement type and does nothing
 */
public class None extends AdvancementRewardType {
	public None() {
		super("");
	}

	/**
	 * Does nothing
	 */
	@Override
	public void onReward(final String value, final Player player) {
		//do nothing...
	}
}
