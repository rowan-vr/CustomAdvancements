package me.tippie.customadvancements.advancement.requirement.types;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AdvancementRequirementType {
	@Getter private final String label;
	@Getter private final String defaultName;
	@Getter private final ItemStack defaultDisplayItem;

	AdvancementRequirementType(final String label, final String defaultName, final ItemStack defaultDisplayItem) {
		this.label = label;
		this.defaultName = defaultName;
		this.defaultDisplayItem = defaultDisplayItem;
	}

	/**
	 * Checks if this requirement is met
	 *
	 * @param value  The value of this requirement
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	public abstract boolean isMet(String value, Player player);

	/**
	 * Checks if this requirement is met and takes executes actions if attempting to activate this requirement
	 *
	 * @param value  The value of this requirement
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	public abstract boolean activate(String value, Player player);

	public boolean equals(final String in) {
		return this.label.equalsIgnoreCase(in);
	}

	public abstract String getMessage(String value, Player player);
}
