package me.tippie.customadvancements.commands;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.player.CAPlayer;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandSet extends SubCommand {

	CommandSet() {
		super("set", "customadvancements.command.set", "Sets the progress of a certain advancement", Lang.COMMAND_SET_PROGRESS_USAGE.getConfigValue(null, true), new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		if (args.length == 2 || args.length == 3 || args.length == 4 || args.length == 5 || args.length == 6) {
			if (args.length == 2) return new ArrayList<>(Arrays.asList("active", "progress", "completed"));
			if (args.length == 3) {
				val trees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
				final List<String> result = new ArrayList<>();
				for (final AdvancementTree tree : trees) result.add(tree.getLabel());
				return result;
			}
			if (args.length == 4) {
				val treeLabel = args[2];
				val tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(treeLabel);
				if (tree == null) return new ArrayList<>();
				val advancements = tree.getAdvancements();
				final List<String> result = new ArrayList<>();
				for (final CAdvancement advancement : advancements) result.add(advancement.getLabel());
				return result;
			}
			if (args.length == 5) {
				val type = args[1];
				if (type.equalsIgnoreCase("active") || type.equalsIgnoreCase("completed")) {
					return new ArrayList<>(Arrays.asList("true", "false"));
				} else {
					return new ArrayList<>();
				}
			}
			return null;
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (args.length == 5 || args.length == 6) {
			val type = args[1];
			val path = args[2] + "." + args[3];
			val value = args[4];
			final CAPlayer player;
			if (args.length == 6) {
				try {
					val uuid = Bukkit.getPlayer(args[5]).getUniqueId();
					player = CustomAdvancements.getCaPlayerManager().getPlayer(uuid);
					if (player == null) throw new NullPointerException();
				} catch (final NullPointerException ex) {
					sender.sendMessage(Lang.COMMAND_PROGRESS_INVALID_PLAYER.getConfigValue(null));
					return;
				}
			} else {
				if (sender instanceof Player) {
					player = CustomAdvancements.getCaPlayerManager().getPlayer(((Player) sender).getUniqueId());
				} else {
					sender.sendMessage("You must provided an username to use this command from the console.");
					return;
				}
			}
			if (type.equalsIgnoreCase("active")) {
				player.getAdvancementProgress().get(path).setActive(Boolean.parseBoolean(value));
				sender.sendMessage(Lang.COMMAND_SET_PROGRESS_RESPONSE.getConfigValue(new String[]{type, args[2], args[3], value}));
			} else if (type.equalsIgnoreCase("completed")) {
				player.getAdvancementProgress().get(path).setCompleted(Boolean.parseBoolean(value));
				sender.sendMessage(Lang.COMMAND_SET_PROGRESS_RESPONSE.getConfigValue(new String[]{type, args[2], args[3], value}));
			} else if (type.equalsIgnoreCase("progress")) {
				try {
					val intValue = Integer.parseInt(value);
					player.getAdvancementProgress().get(path).setProgress(intValue);
					sender.sendMessage(Lang.COMMAND_SET_PROGRESS_RESPONSE.getConfigValue(new String[]{type, args[2], args[3], value}));
				} catch (final NumberFormatException ex) {
					sender.sendMessage(Lang.COMMAND_INVALID_TYPE.getConfigValue(new String[]{value, "an integer"}));
				}
			} else {
				sender.sendMessage(Lang.COMMAND_INVALID_USAGE.getConfigValue(new String[]{getUsage()}));
			}
			CustomAdvancements.getCaPlayerManager().savePlayer(player.getUuid());
		} else {
			sender.sendMessage(Lang.COMMAND_INVALID_USAGE.getConfigValue(null));
		}
	}
}
