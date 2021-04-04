package me.tippie.customadvancements.advancement.requirement;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.types.AdvancementRequirementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdvancementRequirement {
	private final AdvancementRequirementType type;
	private final String value;
	private final ItemStack displayItem;

	public AdvancementRequirement(final String type, final String value, final ItemStack displayItem) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementRequirementType(type);
		this.value = value;
		this.displayItem = (displayItem != null) ? displayItem : this.type.getDefaultDisplayItem();
	}

	public boolean isMet(final Player player) {
		return type.isMet(this.value, player);
	}

	public boolean activate(final Player player) {
		return type.activate(this.value, player);
	}

	public String getNotMetMessage(final Player player) {
		return type.getMessage(this.value, player);
	}
}
