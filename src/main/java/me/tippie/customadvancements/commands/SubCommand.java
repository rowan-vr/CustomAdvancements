package me.tippie.customadvancements.commands;


import lombok.Getter;
import org.bukkit.command.Command;

public abstract class SubCommand {
	@Getter
	private final String label, permission, description;

	SubCommand(final String label, final String permission, final String description) {
		this.label = label;
		this.permission = permission;
		this.description = description;
	}

	public static void execute(final Command command, final String label, final String[] args) {
	}
}
