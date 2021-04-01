package me.tippie.customadvancements.commands;


import lombok.Getter;
import lombok.ToString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents any SubCommand
 */
@ToString
public abstract class SubCommand {

	@Getter private final String label, permission, description;
	@Getter private final List<String> aliases, labels;
	@Getter private static final List<String> subCommands = new ArrayList<>();

	/**
	 * Creates a new {@link SubCommand}
	 *
	 * @param label       The label of the command '/ca <label>'
	 * @param permission  The permission required for this command
	 * @param description The description shown in '/ca help'
	 * @param aliases     Any aliases for this command
	 */
	SubCommand(final String label, final String permission, final String description, final List<String> aliases) {
		this.label = label;
		this.permission = permission;
		this.description = description;
		this.aliases = aliases;
		this.labels = new ArrayList<>(Collections.singletonList(label));
		this.labels.addAll(aliases);
		subCommands.add(label);
	}

	/**
	 * The list of items for tabcompletion
	 */
	public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

	/**
	 * Executed when the command is executed
	 */
	public abstract void execute(final CommandSender sender, final Command command, final String label, final String[] args);
}
