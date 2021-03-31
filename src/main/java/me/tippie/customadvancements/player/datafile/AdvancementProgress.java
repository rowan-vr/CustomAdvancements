package me.tippie.customadvancements.player.datafile;

public class AdvancementProgress {
	private final boolean active;
	private final boolean completed;
	private final int progress;

	public AdvancementProgress(final int progress, final boolean active, final boolean completed) {
		this.progress = progress;
		this.active = active;
		this.completed = completed;
	}
	protected static AdvancementProgress advancementProgressFromFile(final int progress, final boolean active, final boolean completed) {
		return new AdvancementProgress(progress, active, completed);
	}
}
