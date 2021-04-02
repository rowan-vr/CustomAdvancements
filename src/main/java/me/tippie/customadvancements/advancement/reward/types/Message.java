package me.tippie.customadvancements.advancement.reward.types;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message extends AdvancementRewardType {
	public Message() {
		super("message");
	}

	@Override public void onReward(final String value, final Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', value));
	}
}
