package me.tippie.customadvancements.commands;

import me.tippie.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandEmpty extends SubCommand {
	CommandEmpty() {
		super("", null, null);
	}

	public static void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		sender.sendMessage(Lang.COMMAND_EMPTY.getConfigValue(null));
	}
}
