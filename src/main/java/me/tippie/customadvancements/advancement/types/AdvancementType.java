package me.tippie.customadvancements.advancement.types;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.Listener;

import java.util.UUID;

@ToString
public abstract class AdvancementType implements Listener {
	@Getter
	private final String label;

	AdvancementType(final String label) {
		this.label = label;
	}

	public abstract void onProgress();

	public void progress(final int amount, final UUID playeruuid) {

	}

	public boolean equals(String in) {
		return this.label.equals(in);
	}
}
