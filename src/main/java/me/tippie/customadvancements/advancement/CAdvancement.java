package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.advancement.types.AdvancementType;

public class CAdvancement {
	private final AdvancementType type;
	private final int amount;
	private final @Getter
	String label;

	CAdvancement(final String type, final int amount, final String label) {
		this.type = AdvancementManager.getAdvancementType(type);
		this.amount = amount;
		this.label = label;
	}
}
