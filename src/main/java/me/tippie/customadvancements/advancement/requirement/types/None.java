package me.tippie.customadvancements.advancement.requirement.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Placeholder for invalid types, is always met
 */
public class None extends AdvancementRequirementType {
	public None() {
		super("", "", new ItemStack(Material.AIR, 1));
	}

	/**
	 * @return true
	 */
	@Override public boolean isMet(final String value, final Player player) {
		return true;
	}

	/**
	 * @return true
	 */
	@Override public boolean activate(final String value, final Player player) {
		return true;
	}

	/**
	 * @return nothing
	 */
	@Override public String getMessage(final String value, final Player player) {
		return "";
	}
}
