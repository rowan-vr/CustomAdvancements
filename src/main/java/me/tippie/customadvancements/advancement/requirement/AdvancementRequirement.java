package me.tippie.customadvancements.advancement.requirement;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.types.AdvancementRequirementType;
import org.bukkit.entity.Player;

public class AdvancementRequirement {
	private final AdvancementRequirementType type;
	private final String value;

	public AdvancementRequirement(final String type, final String value) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementRequirementType(type);
		this.value = value;
	}

	public boolean isMet(final Player player) {
		return type.isMet(this.value, player);
	}

	public String getNotMetMessage(final Player player) {
		return type.getNotMetMessage(this.value, player);
	}
}
