package me.tippie.customadvancements.advancement.reward;

import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.entity.Player;

/**
 * Represents a reward given after an advancement(tree) is completed.
 */
public class AdvancementReward {
	private final String type;
	private final String value;

	public AdvancementReward(final String type, final String value) {
		this.type = type;
		this.value = value;
	}

	public void onComplete(final Player player) {
		CustomAdvancements.getAdvancementManager().getAdvancementRewardType(this.type).onReward(this.value, player);
	}
}
