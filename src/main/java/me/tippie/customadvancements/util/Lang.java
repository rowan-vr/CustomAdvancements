package me.tippie.customadvancements.util;

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
	COMMAND_INVALID_PLAYER("commands.invalidplayer", "&cThe player you provided is currently not online."),
	COMMAND_INVALID_ADVANCEMENT("commands.invalidadvancement", "&cThe advancement or advancement tree you provided does not exist."),
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
	COMMAND_RELOAD_DESC("commands.reload.desc", "Reloads the configuration, advancements, and messages of the plugin"),
	COMMAND_RELOAD_USAGE("commands.reload.usage", "/ca reload"),
	COMMAND_RELOAD_RESPONSE("commands.reload.response", "&aConfiguration, advancements, and messages are successfully reloaded!"),
	COMMAND_ACTIVATE_DESC("commands.activate.desc", "Activates an advancement of a specific tree if all requirements are met"),
	COMMAND_ACTIVATE_USAGE("commands.activate.usage", "/ca activate <tree> <advancement>"),
	COMMAND_ACTIVATE_RESPONSE_ACTIVATED("commands.activate.response.activated", "&aThe quest has successfully been activated! Good luck!"),
	COMMAND_ACTIVATE_RESPONSE_NOTACTIVATED("commands.activate.response.notactivated", "&cThe quest has not been activated! You do not meet the following requirements:\n"),
	COMMAND_ACTIVATE_RESPONSE_REQUIREMENT_NOTMET("commands.activate.response.requirement_not_met", "&4> &c{0}\n"),
	REQUIREMENT_ADVANCEMENT_MESSAGE("requirements.advancement.message", "Advancement {0} from tree {1} has to be completed."),
	REQUIREMENT_ADVANCEMENT_NAME("requirements.advancement.name", "Advancement"),
	REQUIREMENT_PERMISSION_NAME("requirements.permission.name", "Permission"),
	REQUIREMENT_PERMISSION_MESSAGE("requirements.permission.message", "You need the {0} permission."),
	HELP_HEADER("commands.help.header", "&9---- &dCustomAdvancements Help &9----\n&5Hover commands to see how to use them!"),
	COMMAND_HELP("commands.help.content", "&9> &b{0}&8: &7{1}"),
	COMMAND_HELP_HOVER("commands.help.hover", "&5Command usage: &d{0}\n&e&oClick to run this command."),
	COMMAND_INVALID("commands.invalid", "&cThis subcommand does not exist! Do &e/ca help&c to see all available commands."),
	NO_PERMISSION("commands.no-permission", "&cYou do not have the required permission to use this command. &8({0})"),
	GUI_MAIN_TITLE("guis.main.title", "&5Server specific advancements"),
	GUI_MAIN_TREES_NAME("guis.main.trees.name", "&5&lAdvancement trees"),
	GUI_MAIN_TREES_LORE("guis.main.trees.lore", "&6> &eClick to see all advancement trees!"),
	GUI_MAIN_ACTIVE_NAME("guis.main.active.name", "&a&lActive advancements"),
	GUI_MAIN_ACTIVE_LORE("guis.main.active.lore", "&6> &eClick to see all active advancements!"),
	GUI_MAIN_COMPLETED_NAME("guis.main.completed.name", "&2&lCompleted advancements"),
	GUI_MAIN_COMPLETED_LORE("guis.main.completed.lore", "&6> &eClick to see all completed advancements!"),
	GUI_MAIN_AVAILABLE_NAME("guis.main.available.name", "&5&lAvailable advancements"),
	GUI_MAIN_AVAILABLE_LORE("guis.main.available.lore", "&6> &eClick to see all advancements that can be activated!"),
	GUI_PAGE_NEXT_NAME("guis.page.next.name", "&aNext page"),
	GUI_PAGE_NEXT_LORE("guis.page.next.lore", "&2Click to go to the next page"),
	GUI_PAGE_PREVIOUS_NAME("guis.page.previous.name", "&aPrevious page"),
	GUI_PAGE_PREVIOUS_LORE("guis.page.previous.lore", "&2Click to go to the previous page"),
	GUI_PAGE_FIRST_NAME("guis.page.first.name", "&7&mPrevious page"),
	GUI_PAGE_FIRST_LORE("guis.page.first.lore", "&8You're on the first page"),
	GUI_PAGE_LAST_NAME("guis.page.last.name", "&7&mNext page"),
	GUI_PAGE_LAST_LORE("guis.page.last.lore", "&8You're on the last page"),
	GUI_TREES_TITLE("guis.trees.title", "&5Advancement trees &7({0}/{1})"),
	GUI_TREES_ADVANCEMENTS("guis.trees.advancements", "&6> &eClick to view the advancements"),
	GUI_TREES_INVALID_TREE("guis.trees.invalid_tree", "&cThe tree you tried to open no longer exists!"),
	GUI_ADVANCEMENTS_OPTIONS("guis.advancements.options", "&6> &eClick to view available actions for this advancement"),
	GUI_ADVANCEMENTS_TITLE("guis.advancements.title", "{0} &7({1}/{2})"),
	GUI_ADVANCEMENT_OPTIONS_TITLE("guis.advancement_options.title", "{0}"),
	GUI_ADVANCEMENT_OPTIONS_ACTIVATE_NAME("guis.advancement_options.activate.name", "&aActivate advancement"),
	GUI_ADVANCEMENT_OPTIONS_ACTIVATE_LORE("guis.advancement_options.activate.lore", "&6> &eClick to activate this advancement"),
	GUI_ADVANCEMENT_OPTIONS_ACTIVATED_NAME("guis.advancement_options.activated.name", "&7Activated"),
	GUI_ADVANCEMENT_OPTIONS_ACTIVATED_LORE("guis.advancement_options.activated.lore", "&8This advancement is already active"),
	GUI_ADVANCEMENT_OPTIONS_REQUIREMENTS_NAME("guis.advancement_options.requirements.name", "&cRequirements"),
	GUI_ADVANCEMENT_OPTIONS_REQUIREMENTS_LORE("guis.advancement_options.requirements.lore", "&7You meet {0} out of {1} requirements\n&6>&e Click to see all requirements"),
	GUI_ADVANCEMENT_OPTIONS_COMPLETED_NAME("guis.advancement_options.completed.name", "&2Completed"),
	GUI_ADVANCEMENT_OPTIONS_COMPLETED_LORE("guis.advancement_options.completed.lore", "&aYou've completed this advancement!"),
	GUI_ADVANCEMENT_OPTIONS_NOT_COMPLETED_NAME("guis.advancement_options.not_completed.name", "&4Not completed"),
	GUI_ADVANCEMENT_OPTIONS_NOT_COMPLETED_LORE("guis.advancement_options.not_completed.lore", "&cYou've not completed this advancement yet!"),
	GUI_ADVANCEMENT_OPTIONS_PROGRESS_NAME("guis.advancement_options.progress.name", "&6Progress"),
	GUI_ADVANCEMENT_OPTIONS_PROGRESS_LORE("guis.advancement_options.progress.lore", "&e({3}%) &7{0}&8/&7{1} {2}"),
	GUI_ADVANCEMENT_OPTIONS_ACTIVATED_SUCCESSFULLY_NAME("guis.advancement_options.activated_successfully.name", "&2Successfully activated!"),
	GUI_ADVANCEMENT_OPTIONS_ACTIVATED_SUCCESSFULLY_LORE("guis.advancement_options.activated_successfully.lore", "&aThis advancement is now active! Good luck!"),
	GUI_ADVANCEMENT_OPTIONS_NOT_ACTIVATED_NAME("guis.advancement_options.not_activated.name", "&4Advancement not activated!"),
	GUI_ADVANCEMENT_OPTIONS_NOT_ACTIVATED_LORE("guis.advancement_options.not_activated.lore", "&cYou did not meet {0} out of {1} requirements to activate this advancement"),
	GUI_REQUIREMENTS_NO_REQUIREMENTS_NAME("guis.requirements.no_requirements.name", "&4No requirements"),
	GUI_REQUIREMENTS_NO_REQUIREMENTS_LORE("guis.requirements.no_requirements.lore", "&cThis advancement does not have any requirements"),
	GUI_REQUIREMENTS_TITLE("guis.requirements.title", "{0} &7({1}/{2})"),
	GUI_ACTIVE_TITLE("guis.active.title", "&8Active Advancements &7({0}/{1})"),
	GUI_ACTIVE_NONE_NAME("guis.active.none.name", "&4No active advancements"),
	GUI_ACTIVE_NONE_LORE("guis.active.none.lore", "&cYou currently have no active advancements"),
	GUI_COMPLETED_TITLE("guis.completed.title", "&8Completed Advancements &7({0}/{1})"),
	GUI_COMPLETED_NONE_NAME("guis.completed.none.name", "&4No completed advancements"),
	GUI_COMPLETED_NONE_LORE("guis.completed.none.lore", "&cYou currently have no completed advancements"),
	GUI_AVAILABLE_TITLE("guis.available.title", "&8Available Advancements &7({0}/{1})"),
	GUI_AVAILABLE_NONE_NAME("guis.available.none.name", "&4No available advancements"),
	GUI_AVAILABLE_NONE_LORE("guis.available.none.lore", "&cYou currently have no available advancements"),
	ADVANCEMENT_TYPE_BLOCKBREAK_UNIT("advancements.types.blockbreak.unit", "blocks broken"),
	ADVANCEMENT_TYPE_JOIN_UNIT("advancements.types.join.unit", "times joined"),
	ADVANCEMENT_TYPE_LEAVE_UNIT("advancements.types.leave.unit", "times left"),
	ADVANCEMENT_TYPE_CHAT_UNIT("advancements.types.chat.unit", "messages sent"),
	ADVANCEMENT_TYPE_BLOCKPLACE_UNIT("advancements.types.blockplace.unit", "blocks placed"),
	ADVANCEMENT_TYPE_CATCHFISH_UNIT("advancements.types.catchfish.unit", "things caught"),
	ADVANCEMENT_TYPE_CONSUME_UNIT("advancements.types.consume.unit", "things consumed"),
	ADVANCEMENT_TYPE_STATISTIC_UNIT("advancements.types.statistic.unit", "progress"),
	ADVANCEMENT_TYPE_XPCHANGE_UNIT("advancements.types.xpchange.unit", "XP changed"),
	ADVANCEMENT_TYPE_XPLEVELCHANGE_UNIT("advancements.types.xplevelchange.unit", "level changed"),
	ADVANCEMENT_TYPE_DAMAGETAKEN_UNIT("advancements.type.damagetaken.unit", "damage taken"),
	ADVANCEMENT_TYPE_DAMAGEDEALT_UNIT("advancements.type.damagedealt.unit", "damage dealt"),
	ADVANCEMENT_TYPE_KILLENTITY_UNIT("advancements.type.killentity.unit", "entities killed");

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
