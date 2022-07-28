package me.tippie.customadvancements.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.guis.MainGUI;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the '/ca <SubCommand>' command
 */
public class CommandListener implements CommandExecutor, TabCompleter {

	/**
	 * Set of all registered subcommands.
	 */
	@Getter(AccessLevel.PROTECTED)
	private final Set<SubCommand> subCommands = new HashSet<>();

	public CommandListener() {
		subCommands.add(new CommandHelp());
		subCommands.add(new CommandCheckProgress());
		subCommands.add(new CommandSet());
		subCommands.add(new CommandReload());
		subCommands.add(new CommandActivate());
		subCommands.add(new CommandGUI());
		subCommands.add(new CommandGrantImpossible());
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (label.equalsIgnoreCase("ca") || label.equalsIgnoreCase("customadvancements")) {
			if (args.length == 0) {
				CommandEmpty.execute(sender, command, label, args);
				return true;
			}
			for (final SubCommand subCommand : subCommands) {
				if (subCommand.getLabels().contains(args[0].toLowerCase())) {
					subCommand.run(sender, command, label, args);
					return true;
				}
			}
			sender.sendMessage(Lang.COMMAND_INVALID.getConfigValue(null));
			return true;
		} else if (label.equalsIgnoreCase("advancements") && sender.hasPermission("customadvancements.gui")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be used as a player");
				return true;
			}
			final Player player = (Player) sender;
			try {
				player.openInventory(new MainGUI().getInventory(player));
			} catch (final InvalidAdvancementException ignored) {

			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		if (args.length <= 1) {
			final List<String> result = new ArrayList<>();
			val allowedSubCommands = subCommands.stream().filter(subCommand -> sender.hasPermission(subCommand.getPermission())).collect(Collectors.toList());
			for (final SubCommand subCommand : allowedSubCommands) {
				result.add(subCommand.getLabel());
			}
			return result;
		} else {
			for (final SubCommand subCommand : subCommands) {
				if (subCommand.getLabels().contains(args[0].toLowerCase())) {
					return subCommand.onTabComplete(sender, command, alias, args);
				}
			}
		}
		return new ArrayList<>();
	}
}