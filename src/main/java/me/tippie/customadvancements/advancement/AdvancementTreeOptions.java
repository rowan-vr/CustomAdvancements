package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import me.tippie.customadvancements.guis.TreesGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents the options of an AdvancementTree
 *
 * @see AdvancementTree
 */
@Getter
public class AdvancementTreeOptions {

	/**
	 * Should advancements where the requirements are met automatically be activated once the requirements are met?
	 */
	private final boolean autoActive;

	/**
	 * Type of reward this tree would grant the user on full completion
	 */
	private final List<AdvancementReward> rewards;

	/**
	 * The location of this tree in the tree GUI
	 */
	private final String guiLocation;

	/**
	 * The name of this tree displayed in the GUI
	 */
	private final String displayName;

	/**
	 * The description of this tree displayed in the GUI
	 */
	private final String description;

	/**
	 * The item of this tree in the GUI
	 */
	private final ItemStack displayItem;

	/**
	 * Creates a new instance with {@link AdvancementTreeOptions}
	 *
	 * @param autoActive  Boolean if the advancements in the belonging advancement tree should be automatically be activated if the requirements are met
	 * @param guiLocation The location of this tree in the {@link TreesGUI} formatted as 'page:index', can be 'auto'
	 * @param rewards     List of all the {@link me.tippie.customadvancements.advancement.requirement.AdvancementRequirement}'s the player should be given on completion of all advancements of this tree.
	 * @param displayName The display name of this tree
	 * @param description The description of this tree
	 * @param displayItem The item this tree should have in the GUI's
	 * @see AdvancementTree
	 */
	public AdvancementTreeOptions(final boolean autoActive, final String guiLocation, final List<AdvancementReward> rewards, final String displayName, final String description, final ItemStack displayItem) {
		this.autoActive = autoActive;
		this.rewards = rewards;
		this.guiLocation = guiLocation;
		this.displayName = displayName;
		this.description = description;
		this.displayItem = displayItem;
	}

	/**
	 * Executes the reward actions (give the rewards)
	 *
	 * @param player Player the rewards should be executed for
	 */
	public void onComplete(final Player player) {
		for (final AdvancementReward reward : rewards) {
			reward.onComplete(player);
		}
	}

	/**
	 * Gets the display name of this tree formatted with color coding
	 *
	 * @return String of the display name of this tree
	 */
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}

	/**
	 * Gets the description this tree formatted with color coding
	 *
	 * @return String of the description of this tree
	 */
	public String getDescription() {
		return (description != null) ? ChatColor.translateAlternateColorCodes('&', description) : null;
	}
}
