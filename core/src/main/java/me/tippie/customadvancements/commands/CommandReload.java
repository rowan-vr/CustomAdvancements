package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandReload extends SubCommand {

	CommandReload() {
		super("reload", "customadvancements.command.reload", Lang.COMMAND_RELOAD_DESC.getString(), Lang.COMMAND_RELOAD_USAGE.getString(), new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return new ArrayList<>();
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		CustomAdvancements.getInstance().onDisable();
		CustomAdvancements.getInstance().onEnable();
		sender.sendMessage(Lang.COMMAND_RELOAD_RESPONSE.getString(false));
	}
}
