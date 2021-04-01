package me.tippie.customadvancements.commands;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the '/ca checkprogress' command.
 * This command checks the progress of an advancement.
 */
public class CommandCheckProgress extends SubCommand {

	CommandCheckProgress() {
		super("checkprogress", "customadvancements.command.checkprogress", Lang.COMMAND_DESC_CHECK_PROGRESS.getConfigValue(null, true), new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		if (args.length == 2) {
			val trees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
			List<String> result = new ArrayList<>();
			for (AdvancementTree tree : trees) result.add(tree.getLabel());
			return result;
		} else if (args.length == 3){
			val treeLabel = args[1];
			val advancements = CustomAdvancements.getAdvancementManager().getAdvancementTree(treeLabel).getAdvancements();
			List<String> result = new ArrayList<>();
			for (CAdvancement advancement : advancements) result.add(advancement.getLabel());
			return result;
		} else if (args.length == 4){
			return null;
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {

	}
}
