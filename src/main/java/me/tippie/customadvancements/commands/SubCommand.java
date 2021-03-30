package me.tippie.customadvancements.commands;


import lombok.Getter;
import lombok.ToString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ToString
public abstract class SubCommand {
	@Getter
	private final String label, permission, description;
	@Getter
	private final List<String> aliases, labels;

	SubCommand(final String label, final String permission, final String description, final List<String> aliases) {
		this.label = label;
		this.permission = permission;
		this.description = description;
		this.aliases = aliases;
		this.labels = new ArrayList<>(Collections.singletonList(label));
		this.labels.addAll(aliases);

	}

	public abstract void execute(final CommandSender sender, final Command command, final String label, final String[] args);
}
