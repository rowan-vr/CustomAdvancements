package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class CommandHelp extends SubCommand {
	CommandHelp() {
		super("help", "customadvacements.command.help", Lang.COMMAND_DESC_HELP.getConfigValue(null, true));
	}

	public static void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		Set<SubCommand> subCommands = CustomAdvancements.getInstance().getCommandListener().getSubCommands();
		for (SubCommand subCommand : subCommands) {
			sender.sendMessage("- /" + subCommand.getLabel() + " : " + subCommand.getDescription());
		}
	}
}
