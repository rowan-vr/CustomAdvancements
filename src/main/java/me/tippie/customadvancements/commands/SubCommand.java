package me.tippie.customadvancements.commands;


import lombok.Getter;
import lombok.ToString;
import me.tippie.customadvancements.utils.Lang;
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

	@Getter private final String label, permission, description, usage;
	@Getter private final List<String> aliases, labels;
	@Getter private static final List<String> subCommands = new ArrayList<>();

	/**
	 * Creates a new {@link SubCommand}
	 *
	 * @param label       The label of the command '/ca <label>'
	 * @param permission  The permission required for this command
	 * @param description The description shown in '/ca help'
	 * @param usage How this command should be used.
	 * @param aliases     Any aliases for this command
	 */
	SubCommand(final String label, final String permission, final String description,final String usage, final List<String> aliases) {
		this.label = label;
		this.permission = permission;
		this.description = description;
		this.aliases = aliases;
		this.usage = usage;
		this.labels = new ArrayList<>(Collections.singletonList(label));
		this.labels.addAll(aliases);
		subCommands.add(label);
	}

	/**
	 * The list of items for tabcompletion
	 */
	public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

	/**
	 * Checks if user has correct permission and executes the command, if user doesn't have the correct permission it'll send the configured message.
	 */
	public void run(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender.hasPermission(this.permission)) {
			this.execute(sender, command, label, args);
		} else {
			sender.sendMessage(Lang.NO_PERMISSION.getConfigValue(new String[]{this.permission}));
		}
	}

	/**
	 * Executed when the command is executed and user has correct permission.
	 */
	public abstract void execute(final CommandSender sender, final Command command, final String label, final String[] args);
}
