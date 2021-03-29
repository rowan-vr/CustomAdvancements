package me.tippie.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public enum Lang {
	COMMAND_EMPTY("COMMAND_EMPTY", "You did not use the command right");
	private final String path;
	private final String def;
	private static FileConfiguration LANG;

	Lang(final String path, final String start){
		this.path = path;
		this.def = start;
	}

	public static void setFile(final FileConfiguration config) {
		LANG = config;
	}

	public String getDefault() {
		return this.def;
	}

	public String getPath() {
		return this.path;
	}

	public String getConfigValue(final String[] args) {
		String value = ChatColor.translateAlternateColorCodes('&',
				Objects.requireNonNull(LANG.getString(this.path, this.def)));

		if (args == null)
			return value;
		else {
			if (args.length == 0)
				return value;

			for (int i = 0; i < args.length; i++) {
				value = value.replace("{" + i + "}", args[i]);
			}
		}

		return value;
	}
}
