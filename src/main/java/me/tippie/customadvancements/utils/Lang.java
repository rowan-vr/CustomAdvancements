package me.tippie.customadvancements.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

/**
 * Enum that contains all strings used in messaging players
 */
public enum Lang {
	PREFIX("prefix", "&6[&5Custom&dAdvancements&6]&r"),
	COMMAND_EMPTY("commands.nosubcommand", "&cYou need to provide a sub command. See &e/ca help&c to see all available commands."),
	COMMAND_DESC_HELP("commands.help.description", "Use this command to list all the commands."),
	COMMAND_DESC_CHECK_PROGRESS("commands.checkprogress.description", "Use this command to see the progress of an advancement"),
	HELP_HEADER("commands.help.header", "&9---- &dCustomAdvancements Help &9----"),
	COMMAND_HELP("commands.help.content", "&9> &b{0}&8: &7{1}"),
	COMMAND_INVALID("commands.invalid", "&cThis subcommand does not exist! Do &e/ca help&c to see all available commands."),
	NO_PERMISSION("commands.no-permission", "&cYou do not have the required permission to use this command. &8({0})");
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

	/**
	 * Creates a String for the given enum value and fills arguments in in correct order. {@code 'a {0} b {1}} {0} would be the first given argument and {1} the second.
	 * This includes the prefix
	 *
	 * @param args array of args in correct order
	 * @return String of the requested enum value with placeholders filled in.
	 */
	public String getConfigValue(final String[] args) {
		return getConfigValue(args, false);
	}

	/**
	 * Creates a String for the given enum value and fills arguments in in correct order. {@code 'a {0} b {1}} {0} would be the first given argument and {1} the second
	 *
	 * @param args     array of args in correct order
	 * @param noprefix Boolean if no prefex should be applied to this messages
	 * @return String of the requested enum value with placeholders filled in.
	 */
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
