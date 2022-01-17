package me.tippie.customadvancements.player.datafile;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents Advancement Progression
 *
 * @see me.tippie.customadvancements.player.CAPlayer
 */
@Getter @Setter
public class AdvancementProgress {
	/**
	 * boolean if advancement is active
	 */
	private boolean active;
	/**
	 * boolean if advancement is completed
	 */
	private boolean completed;
	/**
	 * integer of progression
	 */
	private int progress;

	/**
	 * If this quest is unlocked and can be activated
	 */
	private boolean unlocked;

	/**
	 * Creates a new {@link AdvancementProgress}
	 *
	 * @param progress  progress already made
	 * @param active    if this advancement is active
	 * @param completed if this advancement is completed
	 */
	public AdvancementProgress(final int progress, final boolean active, final boolean completed, final boolean unlocked) {
		this.progress = progress;
		this.active = active;
		this.completed = completed;
		this.unlocked = unlocked;
	}
}
