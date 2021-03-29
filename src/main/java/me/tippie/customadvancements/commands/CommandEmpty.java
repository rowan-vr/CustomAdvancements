package me.tippie.customadvancements.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandEmpty implements SubCommand {
	public static void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		sender.sendMessage("");
	}
}
