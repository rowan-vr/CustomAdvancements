package me.tippie.customadvancements.advancement.reward.types;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class AdvancementRewardType {
	@Getter private final String label;

	AdvancementRewardType(final String label) {
		this.label = label;
	}

	public abstract void onReward(String value, Player player);

	public boolean equals(final String in) {
		return this.label.equalsIgnoreCase(in);
	}
}
