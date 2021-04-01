package me.tippie.customadvancements.player.datafile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvancementProgress {
	private boolean active;
	private boolean completed;
	private int progress;

	public AdvancementProgress(final int progress, final boolean active, final boolean completed) {
		this.progress = progress;
		this.active = active;
		this.completed = completed;
	}

	protected static AdvancementProgress advancementProgressFromFile(final int progress, final boolean active, final boolean completed) {
		return new AdvancementProgress(progress, active, completed);
	}
}
