package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.CustomAdvancements;

import java.util.logging.Level;

/**
 * Represents an invalid/empty {@link AdvancementType}
 * This never gets counted. Made to catch an invalid tree configuration
 */
public class Empty extends AdvancementType<Void> {

	/**
	 * Create a new invalid {@link AdvancementType} and log a message to the console.
	 */
	public Empty() {
		super("", "");
		CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "One of your configurations contains an invalid advancement type.");
	}

	/**
	 * Does nothing
	 */
	@Override
	protected void onProgress(final Void event, final String value, final String path) {
		//do nothing
	}
}
