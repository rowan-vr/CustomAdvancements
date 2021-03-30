package me.tippie.customadvancements.advancement;

import me.tippie.customadvancements.advancement.types.AdvancementType;

public class Advancement {
	AdvancementType type;
	int amount;

	Advancement(final String type, final int amount) {
		final String advancementType = type;
		this.amount = amount;
	}
}
