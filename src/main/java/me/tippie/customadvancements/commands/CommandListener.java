package me.tippie.customadvancements.commands;

import lombok.AccessLevel;
import lombok.Getter;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the '/ca <SubCommand>' command
 */
public class CommandListener implements CommandExecutor, TabCompleter {

	/**
	 * Set of all registered subcommands.
	 */
	@Getter(AccessLevel.PROTECTED)
	private final Set<SubCommand> subCommands = new HashSet<>();

	public CommandListener() {
		subCommands.add(new CommandHelp());
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (label.equalsIgnoreCase("ca") || label.equalsIgnoreCase("customadvancements")) {
			if (args.length == 0) {
				CommandEmpty.execute(sender, command, label, args);
				return true;
			}
			for (final SubCommand subCommand : subCommands) {
				if (subCommand.getLabels().contains(args[0].toLowerCase())) {
					subCommand.run(sender, command, label, args);
					return true;
				}
			}
			sender.sendMessage(Lang.COMMAND_INVALID.getConfigValue(null));
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		if (args.length <= 1) {
			return SubCommand.getSubCommands();
		} else {
			for (final SubCommand subCommand : subCommands) {
				if (subCommand.getLabels().contains(args[0].toLowerCase())) {
					return subCommand.onTabComplete(sender, command, alias, args);
				}
			}
		}
		return new ArrayList<>();
	}
}