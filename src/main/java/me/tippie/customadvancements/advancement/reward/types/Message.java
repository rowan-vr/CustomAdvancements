package me.tippie.customadvancements.advancement.reward.types;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Reward type that sends a message to the player
 */
public class Message extends AdvancementRewardType {
	public Message() {
		super("message");
	}

	/**
	 * Sends the message to a player
	 *
	 * @param value  Message that should be sent
	 * @param player {@link Player} will receive this message
	 */
	@Override public void onReward(final String value, final Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', value));
	}
}
