package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents the '/ca' command without any subcommand.
 */
public class CommandEmpty {

	public static void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		sender.sendMessage(Lang.COMMAND_EMPTY.getConfigValue(null));
	}
}
