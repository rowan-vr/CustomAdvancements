package me.tippie.customadvancements.advancement.types;

import lombok.Getter;
import lombok.ToString;
import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ToString
public abstract class AdvancementType implements Listener {
	@Getter
	private final String label;

	AdvancementType(final String label) {
		this.label = label;
	}

	public abstract void onProgress();

	public void progress(final int amount, final UUID playeruuid) {
		val player = CustomAdvancements.getCaPlayerManager().getPlayer(playeruuid);
		for (final AdvancementTree tree : CustomAdvancements.getAdvancementManager().getAdvancementTrees()) {
			final List<CAdvancement> advancements = tree.getAdvancements().stream().filter(advancement -> advancement.getType().equals(this.label)).collect(Collectors.toList());
			for (final CAdvancement advancement : advancements) {
				if (player.checkIfQuestActive(tree.getLabel() + "." + advancement.getLabel()))
					player.updateProgress(tree.getLabel() + "." + advancement.getLabel(), amount, true);
			}
		}
	}

	public boolean equals(final String in) {
		return this.label.equals(in);
	}
}
