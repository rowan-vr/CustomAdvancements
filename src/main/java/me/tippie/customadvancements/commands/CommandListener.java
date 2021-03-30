package me.tippie.customadvancements.commands;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class CommandListener implements CommandExecutor {
	@Getter(AccessLevel.PROTECTED)
	private final Set<SubCommand> subCommands = new HashSet<>();

	public CommandListener() {
		subCommands.add(new CommandEmpty());
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
				System.out.println("Checking:" + subCommand.getLabel());
				if (subCommand.getLabel().equals(args[0])) {
					System.out.println("Executing:" + subCommand.getLabel());
					SubCommand.execute(command, label, args);
					return true;
				}
			}
		}
		return false;
	}
}