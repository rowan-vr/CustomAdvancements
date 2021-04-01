package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandCheckProgress extends SubCommand {

	CommandCheckProgress() {
		super("checkprogress", "customadvancements.command.checkprogress", Lang.COMMAND_DESC_CHECK_PROGRESS.getConfigValue(null), new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {

	}
}
