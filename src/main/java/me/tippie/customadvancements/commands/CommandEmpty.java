package me.tippie.customadvancements.commands;

import me.tippie.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandEmpty {

	public static void execute(final CommandSender sender, Command command, final String label, final String[] args) {
		sender.sendMessage(Lang.COMMAND_EMPTY.getConfigValue(null));
	}
}
