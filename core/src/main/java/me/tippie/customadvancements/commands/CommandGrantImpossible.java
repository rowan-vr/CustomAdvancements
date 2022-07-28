package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.types.Impossible;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandGrantImpossible extends SubCommand{
	CommandGrantImpossible() {
		super("grantimpossible", "customadvancements.command.grantimpossible", Lang.COMMAND_GRANTIMPOSSIBLE_DESC.getString(), Lang.COMMAND_GRANTIMPOSSIBLE_USAGE.getString(), new ArrayList<>());
	}

	@Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}

	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(Lang.COMMAND_INVALID_USAGE.getConfigValue(new String[]{getUsage()}));
			return;
		}

		OfflinePlayer player = Bukkit.getPlayer(args[1]);
		if (player == null) {
			try {
				player = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
			} catch (IllegalArgumentException ignored){}
		}
		if (player == null) {
			sender.sendMessage(Lang.COMMAND_INVALID_PLAYER.getConfigValue(null));
			return;
		}
		try {
			int amount = Integer.parseInt(args[1]);
			Impossible.progress(player,amount);
			sender.sendMessage(Lang.COMMAND_GRANTIMPOSSIBLE_SUCCESS.getConfigValue(null));
		} catch (IllegalArgumentException e) {
			sender.sendMessage(Lang.COMMAND_GRANTIMPOSSIBLE_NOT_A_NUMBER.getConfigValue(null));
		}

	}
}
