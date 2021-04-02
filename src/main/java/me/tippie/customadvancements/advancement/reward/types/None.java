package me.tippie.customadvancements.advancement.reward.types;

import org.bukkit.entity.Player;

public class None extends AdvancementRewardType {

	public None() {
		super("");
	}

	@Override
	public void onReward(final String value, final Player player) {
		//do nothing...
	}
}
