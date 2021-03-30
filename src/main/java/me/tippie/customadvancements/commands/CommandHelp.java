package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CommandHelp extends SubCommand {
	CommandHelp() {
		super("help", "customadvacements.command.help", Lang.COMMAND_DESC_HELP.getConfigValue(null, true), new ArrayList<>(Arrays.asList("?", "commands")));
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		System.out.println("Exucuted help command.");
		final Set<SubCommand> subCommands = CustomAdvancements.getCommandListener().getSubCommands();
		sender.sendMessage(Lang.HELP_HEADER.getConfigValue(null, true));
		for (final SubCommand subCommand : subCommands) {
			System.out.println(subCommand);
			sender.sendMessage(Lang.COMMAND_HELP.getConfigValue(new String[]{"/ca " + subCommand.getLabel(), subCommand.getDescription()}, true));
		}
	}
}
