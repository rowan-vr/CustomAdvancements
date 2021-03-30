package me.tippie.customadvancements.player.datafile;

import java.util.UUID;

public class AdvancementProgress {
	private final boolean started;
	private final boolean completed;
	private final Object progress;
	private UUID playeruuid;

	public AdvancementProgress(final Object progress, final boolean started, final boolean completed) {
		this.progress = progress;
		this.started = started;
		this.completed = completed;
	}
}
