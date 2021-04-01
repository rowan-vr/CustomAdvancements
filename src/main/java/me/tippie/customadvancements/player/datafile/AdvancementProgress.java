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
	 * Creates a new {@link AdvancementProgress}
	 *
	 * @param progress  progress already made
	 * @param active    if this advancement is active
	 * @param completed if this advancement is completed
	 */
	public AdvancementProgress(final int progress, final boolean active, final boolean completed) {
		this.progress = progress;
		this.active = active;
		this.completed = completed;
	}

	/**
	 * @deprecated use {@code new AdvancementProgress(progress, active, completed} instead.
	 */
	protected static AdvancementProgress advancementProgressFromFile(final int progress, final boolean active, final boolean completed) {
		return new AdvancementProgress(progress, active, completed);
	}
}
