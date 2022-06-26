package me.tippie.customadvancements.advancement;

/**
 * Represents the exception when there's no valid advancement path, tree label or advancement label is given as input
 */
public class InvalidAdvancementException extends Throwable {
	private static final long serialVersionUID = -7942217389301775047L;

	public InvalidAdvancementException() {
		super();
	}

	public InvalidAdvancementException(String message) {
		super(message);
	}
}
