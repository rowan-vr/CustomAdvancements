package me.tippie.customadvancements.advancement.requirement.types;

import org.bukkit.entity.Player;

public class None extends AdvancementRequirementType {
	public None() {
		super("");
	}

	@Override public boolean isMet(final String value, final Player player) {
		return true;
	}

	@Override public String getNotMetMessage(String value, Player player) {
		return "";
	}
}
