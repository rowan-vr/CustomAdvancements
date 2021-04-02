package me.tippie.customadvancements.advancement.reward.types;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ConsoleCommand extends AdvancementRewardType {
	public ConsoleCommand() {
		super("consolecommand");
	}

	@Override public void onReward(final String value, final Player player) {
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, value.replaceAll("%player%", player.getName()));
	}
}
