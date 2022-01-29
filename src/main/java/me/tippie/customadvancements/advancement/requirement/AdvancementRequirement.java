package me.tippie.customadvancements.advancement.requirement;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.types.AdvancementRequirementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an requirement for an {@link me.tippie.customadvancements.advancement.CAdvancement}
 */
public class AdvancementRequirement {
	/**
	 * The {@link AdvancementRequirementType} of this requirement
	 */
	@Getter private final AdvancementRequirementType type;

	/**
	 * The value of this requirement
	 */
	@Getter private final String value;

	/**
	 * The name of this requirement
	 */
	@Getter private final String name;

	/**
	 * The {@link ItemStack} for the display item of this requirement in GUI's
	 */
	@Getter private final ItemStack displayItem;

	/**
	 * The message shown in GUI's for the requirement that must be reached, for example: 'Completed quest a' or 'Have x rank'
	 */
	@Getter private final String message;

	/**
	 * Creates a new {@link AdvancementRequirement}
	 *
	 * @param type        The label of the {@link AdvancementRequirementType}
	 * @param value       The value of the requirement
	 * @param name        The name of this requirement, when null it will use the default of the type
	 * @param message     The message of this requirement, when null it will use the default of the type
	 * @param displayItem The {@link ItemStack} of this requirement, when null it will use the default of the type
	 */
	public AdvancementRequirement(final String type, final String value, final String name, final String message, final ItemStack displayItem) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementRequirementType(type);
		this.value = value;
		this.name = (name != null) ? name : this.type.getDefaultName();
		this.message = message;
		this.displayItem = (displayItem != null) ? displayItem : this.type.getDefaultDisplayItem();
	}

	/**
	 * Checks if a {@link Player} meets this requirement
	 *
	 * @param player the player that should be checked for
	 * @return boolean if the given player meets this requirement
	 */
	public boolean isMet(final Player player) {
		return type.isMet(this.value, player);
	}

	/**
	 * Activates actions for this requirement, for example takes items when a quest is activated
	 *
	 * @param player The player the actions should be done for
	 * @return boolean if the actions are successfully activated or not
	 */
	public boolean activate(final Player player) {
		return type.activate(this.value, player);
	}

	/**
	 * Gets the requirement message for this requirement
	 *
	 * @param player The player the message should be get for
	 * @return String of the message
	 */
	public String getMessage(final Player player) {
		return (message != null) ? message : type.getMessage(this.value, player);
	}
}
