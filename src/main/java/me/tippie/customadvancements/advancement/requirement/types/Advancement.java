package me.tippie.customadvancements.advancement.requirement.types;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

/**
 * The advancement requirement type
 */
public class Advancement extends AdvancementRequirementType {
	public Advancement() {
		super("advancement", Lang.REQUIREMENT_ADVANCEMENT_NAME.getString(), new ItemStack(Material.BOOK, 1));
	}

	/**
	 * Checks if this requirement is met
	 *
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	@Override public boolean isMet(final String path, final Player player) {
		val caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());
		try {
			return caPlayer.checkIfAdvancementCompleted(path);
		} catch (final InvalidAdvancementException ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "An advancement requirement has an invalid advancement as requirement: " + path);
			return false;
		}
	}

	/**
	 * Checks if this requirement is met and takes executes actions if attempting to activate this requirement
	 *
	 * @param value  The value of this requirement
	 * @param player The player this requirement is checked on
	 * @return boolean if the requirement is met
	 */
	@Override public boolean activate(final String value, final Player player) {
		return isMet(value, player);
	}

	/**
	 * Gets the default message of this requirement type
	 *
	 * @param path   The path of the advancement
	 * @param player The player the message should be gotten for
	 * @return String of the default message
	 */
	@Override public String getMessage(final String path, final Player player) {
		try {
			val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
			val tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(advancement.getTree());
			return Lang.REQUIREMENT_ADVANCEMENT_MESSAGE.getConfigValue(new String[]{advancement.getLabel(), tree.getLabel()}, true);
		} catch (final Throwable ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.WARNING, ex.getMessage());
			return "Error whilst loading this message, check console.";
		}
	}
}
