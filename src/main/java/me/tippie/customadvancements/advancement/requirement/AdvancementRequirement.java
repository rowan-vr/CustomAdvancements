package me.tippie.customadvancements.advancement.requirement;

import lombok.Getter;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.types.AdvancementRequirementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdvancementRequirement {
	private final AdvancementRequirementType type;
	private final String value;
	@Getter private final String name;
	@Getter private final ItemStack displayItem;
	private final String message;

	public AdvancementRequirement(final String type, final String value, final String name, final String message, final ItemStack displayItem) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementRequirementType(type);
		this.value = value;
		this.name = (name != null) ? name : this.type.getDefaultName();
		this.message = message;
		this.displayItem = (displayItem != null) ? displayItem : this.type.getDefaultDisplayItem();
	}

	public boolean isMet(final Player player) {
		return type.isMet(this.value, player);
	}

	public boolean activate(final Player player) {
		return type.activate(this.value, player);
	}

	public String getMessage(final Player player) {
		return (message != null) ? message : type.getMessage(this.value, player);
	}
}
