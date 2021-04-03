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

	public AdvancementReward(final String type, final String value) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementRewardType(type);
		this.value = value;
	}

	public void onComplete(final Player player) {
		type.onReward(this.value, player);
	}
}
