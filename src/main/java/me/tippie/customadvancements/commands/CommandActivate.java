package me.tippie.customadvancements.commands;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandActivate extends SubCommand {

	CommandActivate() {
		super("activate", "customadvancements.command.activate", Lang.COMMAND_ACTIVATE_DESC.getString(), Lang.COMMAND_ACTIVATE_USAGE.getString(), new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		if (args.length == 2) {
			val trees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
			final List<String> result = new LinkedList<>();
			for (final AdvancementTree tree : trees) result.add(tree.getLabel());
			return result;
		} else if (args.length == 3) {
			val treeLabel = args[1];
			val tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(treeLabel);
			if (tree == null) return new ArrayList<>();
			val advancements = tree.getAdvancements();
			final List<String> result = new LinkedList<>();
			for (final CAdvancement advancement : advancements) result.add(advancement.getLabel());
			return result;
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be used by players.");
			return;
		}
		if (args.length != 3) {
			sender.sendMessage(Lang.COMMAND_INVALID_USAGE.getConfigValue(new String[]{this.getUsage()}));
			return;
		}
		val path = args[1] + "." + args[2];
		try {
			val result = CustomAdvancements.getCaPlayerManager().getPlayer(((Player) sender).getUniqueId()).activateAdvancement(path);
			if (result == null) {
				sender.sendMessage(Lang.COMMAND_ACTIVATE_RESPONSE_ACTIVATED.getString(false));
			} else {
				sender.sendMessage(Lang.COMMAND_ACTIVATE_RESPONSE_NOTACTIVATED.getString(false));
				for (final AdvancementRequirement requirement : result) {
					sender.sendMessage(Lang.COMMAND_ACTIVATE_RESPONSE_REQUIREMENT_NOTMET.getConfigValue(new String[]{requirement.getNotMetMessage((Player) sender)},true));
				}
			}
		} catch (final InvalidAdvancementException ex) {
			sender.sendMessage(Lang.COMMAND_INVALID_ADVANCEMENT.getString(false));
		}
	}
}
