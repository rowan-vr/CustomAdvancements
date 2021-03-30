package me.tippie.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public enum Lang {
	PREFIX("PREFIX", "&6[&5Custom&dAdvancements&6]&r"),
	COMMAND_EMPTY("COMMAND_EMPTY", "&4You did not use the command right."),
	COMMAND_DESC_HELP("COMMAND_DESC_HELP", "Use this command to list all the commands."),
	HELP_HEADER("HELP_HEADER", "&9---- &dCustomAdvancements Help &9----"),
	COMMAND_HELP("COMMAND_HELP", "&9> &b{0}&8: &7{1}");
	private final String path;
	private final String def;
	private static FileConfiguration LANG;

	Lang(final String path, final String start) {
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
		return getConfigValue(args, false);
	}

	public String getConfigValue(final String[] args, final boolean noprefix) {
		String value;
		try {
			value = ChatColor.translateAlternateColorCodes('&',
					Objects.requireNonNull(LANG.getString(this.path, this.def)));
		} catch (final NullPointerException e) {
			System.out.println(e.getMessage());
			return "Failed to load string.";
		}
		final String prefix = ChatColor.translateAlternateColorCodes('&',
				Objects.requireNonNull(LANG.getString("PREFIX", "&6[&5Custom&dAdvancements&6]&r")));

		value = ((noprefix) ? "" : prefix + " ") + value;

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
