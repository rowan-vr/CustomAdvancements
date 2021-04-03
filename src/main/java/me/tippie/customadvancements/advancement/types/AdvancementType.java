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

/**
 * Represents a type of an {@link CAdvancement}
 */
@ToString
public abstract class AdvancementType implements Listener {

	/**
	 * The label of this {@link AdvancementType}
	 */
	@Getter private final String label;

	/**
	 * Creates a new {@link AdvancementType}
	 *
	 * @param label label of this type
	 * @see me.tippie.customadvancements.advancement.AdvancementManager#registerAdvancement(AdvancementType)
	 */
	AdvancementType(final String label) {
		this.label = label;
	}

	/**
	 * Registers progress of an {@link AdvancementType}
	 *
	 * @param amount     the amount the progress should change with, can be negative
	 * @param playeruuid the uuid of the player whose progress must change
	 */
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

	/**
	 * Compares string with {@link AdvancementType}
	 *
	 * @param in string of type (label)
	 * @return if type label equals to label of this {@link AdvancementType}
	 */
	public boolean equals(final String in) {
		return this.label.equals(in);
	}
}
