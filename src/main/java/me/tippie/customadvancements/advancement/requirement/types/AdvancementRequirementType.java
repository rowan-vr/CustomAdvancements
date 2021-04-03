package me.tippie.customadvancements.advancement.requirement.types;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class AdvancementRequirementType {
	@Getter private final String label;

	AdvancementRequirementType(final String label) {
		this.label = label;
	}

	/**
	 * Checks if this requirement is met
	 *
	 * @param value  The value of this requirement
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	public abstract boolean isMet(String value, Player player);

	public boolean equals(final String in) {
		return this.label.equalsIgnoreCase(in);
	}

	public abstract String getNotMetMessage(String value, Player player);
}
