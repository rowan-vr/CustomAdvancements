package me.tippie.customadvancements.advancement.reward;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.reward.types.AdvancementRewardType;
import org.bukkit.entity.Player;

/**
 * Represents a reward given after an advancement(tree) is completed.
 */
public class AdvancementReward {
	private final AdvancementRewardType type;
	private final String value;

	/**
	 * Creates a new {@link AdvancementReward}
	 *
	 * @param type  The type of the reward
	 * @param value The value of the reward
	 */
	public AdvancementReward(final String type, final String value) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementRewardType(type);
		this.value = value;
	}

	/**
	 * Complete this reward and executes the actions for this the {@link AdvancementRewardType}
	 *
	 * @param player the player this reward should be executed for
	 */
	public void onComplete(final Player player) {
		type.onReward(this.value, player);
	}
}
