package me.tippie.customadvancements.advancement.reward.types;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Reward type that executes a console command onReward
 */
public class ConsoleCommand extends AdvancementRewardType {
	/**
	 * Creates a new console command reward type
	 */
	public ConsoleCommand() {
		super("consolecommand");
	}

	/**
	 * Executes the console command
	 *
	 * @param value  String of the command that should be executed not starting with a /
	 * @param player {@link Player} this reward should be executed for
	 */
	@Override public void onReward(final String value, final Player player) {
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, value.replaceAll("%player%", player.getName()));
	}
}
