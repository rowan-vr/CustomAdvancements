package me.tippie.customadvancements.advancement.requirement.types;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Permission extends AdvancementRequirementType {
	Permission() {
		super("permission", Lang.REQUIREMENT_PERMISSION_NAME.getString(), new ItemStack(Material.NAME_TAG, 1));
	}

	/**
	 * Checks if this requirement is met
	 *
	 * @param value  The value of this requirement
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	@Override public boolean isMet(final String value, final Player player) {
		return player.hasPermission(value);
	}

	/**
	 * Checks if this requirement is met and takes executes actions if attempting to activate this requirement
	 *
	 * @param value  The value of this requirement
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	@Override public boolean activate(final String value, final Player player) {
		return player.hasPermission(value);
	}

	/**
	 * Gets the default message of this requirement type
	 *
	 * @param value  The value of the requirement
	 * @param player The player the message should be gotten for
	 * @return String of the default message
	 */
	@Override public String getMessage(final String value, final Player player) {
		return Lang.REQUIREMENT_PERMISSION_MESSAGE.getConfigValue(new String[]{value}, true);
	}
}
