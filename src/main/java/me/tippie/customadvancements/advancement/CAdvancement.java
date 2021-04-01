package me.tippie.customadvancements.advancement;

import lombok.Getter;
import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import org.bukkit.Bukkit;

import java.util.UUID;

public class CAdvancement {
	@Getter
	private final AdvancementType type;
	@Getter
	private final int maxProgress;
	@Getter
	private final String label;

	CAdvancement(final String type, final int maxProgress, final String label) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementType(type);
		this.maxProgress = maxProgress;
		this.label = label;
	}

	public void complete(final UUID uuid, final String treeLabel) {
		val player = Bukkit.getPlayer(uuid);
		assert player != null;
		player.sendMessage("You completed quest '" + label + "' from tree '" + treeLabel + "'");
	}
}
