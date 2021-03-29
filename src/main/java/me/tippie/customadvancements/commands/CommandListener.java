package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
		}
		if (label.equalsIgnoreCase("ca") || label.equalsIgnoreCase("customadvancements")) {
			if (args.length == 0) {
				CommandEmpty.execute(sender, command, label, args);
				return true;
			} else {
				final String subcommand = args[0];
			}
		}
		return false;
	}
}
