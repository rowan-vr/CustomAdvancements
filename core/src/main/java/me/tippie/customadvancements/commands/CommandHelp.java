package me.tippie.customadvancements.commands;

import lombok.val;
import lombok.var;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.util.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Represents the '/ca help' command.
 */
public class CommandHelp extends SubCommand {
	CommandHelp() {
		super("help", "customadvancements.command.help", Lang.COMMAND_DESC_HELP.getConfigValue(null, true), "/ca help", new ArrayList<>(Arrays.asList("?", "commands")));
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return new ArrayList<>();
	}

	@Override
	public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {
		final Set<SubCommand> subCommands = CustomAdvancements.getCommandListener().getSubCommands();
		sender.sendMessage(Lang.HELP_HEADER.getConfigValue(null, true));
		for (final SubCommand subCommand : subCommands) {
			if (sender.hasPermission(subCommand.getPermission())) {
				var message = new TextComponent(Lang.COMMAND_HELP.getConfigValue(new String[]{"/ca " + subCommand.getLabel(), subCommand.getDescription()}, true));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ca " + subCommand.getLabel()));
				message = CustomAdvancements.getInternals().setHoverText(message, Lang.COMMAND_HELP_HOVER.getConfigValue(new String[]{subCommand.getUsage()}, true));
				sender.spigot().sendMessage(message);
			}
		}
	}
}
