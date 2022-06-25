package me.tippie.customadvancements;

import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPICustomAdvancementsExpansion extends PlaceholderExpansion {

	private final CustomAdvancements plugin = CustomAdvancements.getInstance();

	public PAPICustomAdvancementsExpansion() {

	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override public @NotNull String getIdentifier() {
		return "customadvancements";
	}

	@Override public @NotNull String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override public @NotNull String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(final Player player, @NotNull final String identifier) {

		if (player == null) {
			return "";
		}
		val caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());

		if (caPlayer == null) {
			return "";
		}

		if (identifier.equalsIgnoreCase("available_advancements")) {
			return String.valueOf(caPlayer.getAvailableAdvancements().size());
		}

		if (identifier.startsWith("available_advancements_")) {
			val a = identifier.split("_");
			val tree = a[a.length - 1];
			try {
				return String.valueOf(caPlayer.getAvailableAdvancements(tree).size());
			} catch (final InvalidAdvancementException ex) {
				return ChatColor.DARK_RED + "Invalid tree provided in placeholder!";
			}
		}

		if (identifier.equalsIgnoreCase("completed_advancements")) {
			return String.valueOf(caPlayer.getCompletedAdvancements().size());
		}

		if (identifier.startsWith("completed_advancements_")) {
			val a = identifier.split("_");
			val tree = a[a.length - 1];
			try {
				return String.valueOf(caPlayer.getCompletedAdvancements(tree).size());
			} catch (final InvalidAdvancementException ex) {
				return ChatColor.DARK_RED + "Invalid tree provided in placeholder!";
			}
		}

		if (identifier.equalsIgnoreCase("active_advancements")) {
			return String.valueOf(caPlayer.getActiveAdvancements().size());
		}

		if (identifier.startsWith("active_advancements_")) {
			val a = identifier.split("_");
			val tree = a[a.length - 1];
			try {
				return String.valueOf(caPlayer.getActiveAdvancements(tree).size());
			} catch (final InvalidAdvancementException ex) {
				return ChatColor.DARK_RED + "Invalid tree provided in placeholder!";
			}
		}

		if (identifier.startsWith("progress_percentage_")) {
			val a = identifier.split("_");
			val path = a[a.length - 1];
			try {
				val progress = caPlayer.getProgress(path);
				val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
				return String.valueOf(Math.round(((double) progress / (double) advancement.getMaxProgress()) * 100));
			} catch (final InvalidAdvancementException ex) {
				return ChatColor.DARK_RED + "Invalid path provided in placeholder!";
			}
		}


		if (identifier.startsWith("progress_")) {
			val a = identifier.split("_");
			val path = a[a.length - 1];
			try {
				return String.valueOf(caPlayer.getProgress(path));
			} catch (InvalidAdvancementException e){
				return ChatColor.DARK_RED + "Invalid path provided in placeholder!";

			}
		}

		if (identifier.startsWith("max_progress_")) {
			val a = identifier.split("_");
			val path = a[a.length - 1];
			try {
				val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
				return String.valueOf(advancement.getMaxProgress());
			} catch (final InvalidAdvancementException ex) {
				return ChatColor.DARK_RED + "Invalid path provided in placeholder!";
			}
		}

		if (identifier.startsWith("meet_requirements_")) {
			val a = identifier.split("_");
			val path = a[a.length - 1];
			try {
				val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
				return String.valueOf(advancement.meetRequirements(player));
			} catch (final InvalidAdvancementException ex) {
				return ChatColor.DARK_RED + "Invalid path provided in placeholder!";
			}
		}


		// We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
		// was provided
		return null;
	}
}
