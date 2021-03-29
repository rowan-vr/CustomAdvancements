package me.tippie.utils;

import org.bukkit.entity.Player;

import java.util.Objects;

public class Utils {
	public static boolean hasPermission(final Player player, final String permission){
		if(!player.hasPermission(permission)) {
			Objects.requireNonNull(player.getPlayer()).sendMessage("You do not have the required permission.");
			return false;
		} else {
			return true;
		}
	}
}
