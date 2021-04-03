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
	COMMAND_INVALID_PLAYER("commands.invalidplayer", "&cThe player you provided has never join the server or is not online."),
	COMMAND_INVALID_ADVANCEMENT("commands.invalidadvancement", "&cThe advancement or advancementtree provided does not exist."),
	COMMAND_CHECK_PROGRESS_USAGE("commands.checkprogress.usage", "/ca checkprogress <tree> <advancement> [player]"),
	COMMAND_INVALID_USAGE("commands.invalid_usage", "&cYou used this command wrong! Correct usage: &7{0}"),
	COMMAND_INVALID_TYPE("commands.invalid_type", "'{0}' must be {1}"),
	COMMAND_CHECK_PROGRESS_HEADER("commands.checkprogress.header", "&6---- &9Advancement Progress &6----"),
	COMMAND_CHECK_PROGRESS_USER("commands.checkprogress.user", "&dUser: &5{0}"),
	COMMAND_CHECK_PROGRESS_TREE("commands.checkprogress.tree", "&dTree: &5{0}"),
	COMMAND_CHECK_PROGRESS_ADVANCEMENT("commands.checkprogress.advancement", "&dAdvancement: &5{0}"),
	COMMAND_CHECK_PROGRESS_PROGRESS("commands.checkprogress.progress", "&3Progress: &9{0}&8/&9{1}"),
	COMMAND_CHECK_PROGRESS_ACTIVE("commands.checkprogress.active", "&3Active: &9{0}"),
	COMMAND_CHECK_PROGRESS_COMPLETED("commands.checkprogress.completed", "&3Completed: &9{0}"),
	COMMAND_SET_PROGRESS_USAGE("commands.set.usage", "/ca set <active|progress|completed> <tree> <advancement> <value> [player]"),
	COMMAND_SET_PROGRESS_RESPONSE("commands.set.response", "&aSucessfully set {0} of advancement {1}.{2} to {3}"),
	COMMAND_RELOAD_DESC("commands.reload.desc", "Reloads the configuration, advancements and messages of the plugin"),
	COMMAND_RELOAD_USAGE("commands.reload.usage", "/ca reload"),
	COMMAND_RELOAD_RESPONSE("commands.reload.response", "&aConfiguration, advancements and messages are successfully reloaded!"),
	COMMAND_ACTIVATE_DESC("commands.activate.desc", "Activates a advancement of a specific tree if all requirements are met"),
	COMMAND_ACTIVATE_USAGE("commands.activate.usage", "/ca activate <tree> <advancement>"),
	COMMAND_ACTIVATE_RESPONSE_ACTIVATED("commands.activate.response.activated", "&aThe quest has successfully been activated! Goodluck!"),
	COMMAND_ACTIVATE_RESPONSE_NOTACTIVATED("commands.activate.response.notactivated", "&cThe quest has not been activated! You do not meet the following requirements:\n"),
	COMMAND_ACTIVATE_RESPONSE_REQUIREMENT_NOTMET("commands.activate.response.requirement_not_met", "&4> &c{0}\n"),
	REQUIREMENT_ADVANCEMENT_NOTMET("requirements.advancement.not_met", "Advancement {0} from tree {1} has to be completed."),
	HELP_HEADER("commands.help.header", "&9---- &dCustomAdvancements Help &9----\n&5Hover commands to see how to use them!"),
	COMMAND_HELP("commands.help.content", "&9> &b{0}&8: &7{1}"),
	COMMAND_HELP_HOVER("commands.help.hover", "&5Command usage: &d{0}\n&e&oClick to run this command."),
	COMMAND_INVALID("commands.invalid", "&cThis subcommand does not exist! Do &e/ca help&c to see all available commands."),
	NO_PERMISSION("commands.no-permission", "&cYou do not have the required permission to use this command. &8({0})"),
	GUI_MAIN_TITLE("guis.main.title", "&5Server specific advancements"),
	GUI_MAIN_TREES_NAME("guis.main.trees.name", "&5&lAdvancement trees"),
	GUI_MAIN_TREES_LORE("guis.main.trees.lore", "&6> &eClick to see all advancement trees!"),
	GUI_PAGE_NEXT_NAME("guis.page.next.name", "&aNext page"),
	GUI_PAGE_NEXT_LORE("guis.page.next.lore", "&2Click to go to the next page"),
	GUI_PAGE_PREVIOUS_NAME("guis.page.previous.name", "&aPrevious page"),
	GUI_PAGE_PREVIOUS_LORE("guis.page.previous.lore", "&2Click to go to the previous page"),
	GUI_PAGE_FIRST_NAME("guis.page.first.name", "&7&mPrevious page"),
	GUI_PAGE_FIRST_LORE("guis.page.first.lore", "&8You're on the first page"),
	GUI_PAGE_LAST_NAME("guis.page.last.name", "&7&mNext page"),
	GUI_PAGE_LAST_LORE("guis.page.last.lore", "&8You're on the last page"),
	GUI_TREES_TITLE("guis.trees.title", "&5Advancement trees &7({0}/{1})");


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
	 * Gets the string without prefix
	 * @return the string without prefix
	 */
	public String getString(){
		return this.getConfigValue(null, true);
	}

	public String getString(final boolean noprefix){
		return this.getConfigValue(null, noprefix);
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
