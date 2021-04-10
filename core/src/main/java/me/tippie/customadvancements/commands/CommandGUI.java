package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.guis.MainGUI;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandGUI extends SubCommand {

	CommandGUI() {
		super("gui", "customadvancements.gui", Lang.COMMAND_GUI_DESC.getString(), Lang.COMMAND_GUI_USAGE.getString(), new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return new ArrayList<>();
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be used as player!");
			return;
		}
		final Player player = (Player) sender;
		try {
			player.openInventory(new MainGUI().getInventory(player));
		} catch (InvalidAdvancementException ignored) {

		}
	}
}
