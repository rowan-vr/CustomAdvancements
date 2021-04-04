package me.tippie.customadvancements.player;

import lombok.Getter;
import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.player.datafile.AdvancementProgress;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player
 */
public class CAPlayer {
	/**
	 * The UUID of this player.
	 */
	@Getter private final UUID uuid;

	/**
	 * Map with the path of an advancement with their {@link AdvancementProgress}.
	 */
	@Getter private final Map<String, AdvancementProgress> advancementProgress;

	/**
	 * The {@link AdvancementProgressFile} of this player.
	 */
	@Getter private final AdvancementProgressFile advancementProgressFile;

	/**
	 * Creates a new {@link CAPlayer} and loads their progress.
	 *
	 * @param playeruuid the UUID of the player
	 */
	CAPlayer(final UUID playeruuid) {
		advancementProgressFile = new AdvancementProgressFile(playeruuid);
		advancementProgress = advancementProgressFile.loadFile();
		uuid = playeruuid;
	}

	/**
	 * Updates progress for any {@link me.tippie.customadvancements.advancement.CAdvancement}
	 * for this player with a certain amount and optionally checks if it is completed right away.
	 *
	 * @param path             the path of the completed advancement formatted as 'treeLabel.advancementLabel'
	 * @param amount           amount of increasement or decreasement
	 * @param checkIfCompleted boolean if this advancement should be check completed after progress is set
	 */
	public void updateProgress(final String path, final int amount, final boolean checkIfCompleted) throws InvalidAdvancementException {
		val progress = advancementProgress.get(path);
		progress.setProgress(progress.getProgress() + amount);
		if (checkIfCompleted) checkCompleted(path);
	}

	/**
	 * Checks if quest is active for this player
	 *
	 * @param path the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return boolean if the quest is active
	 */
	public boolean checkIfAdvancementActive(final String path) throws InvalidAdvancementException {
		val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
		return advancementProgress.get(path).isActive() || (!advancementProgress.get(path).isCompleted() && CustomAdvancements.getAdvancementManager().getAdvancementTree(path.split("\\.")[0]).getOptions().isAutoActive() && advancement.meetRequirements(Bukkit.getPlayer(this.uuid)));
	}

	/**
	 * Checks if this {@link CAPlayer} completed a quest
	 *
	 * @param path the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return boolean if the quest is completed
	 * @see CAPlayer#checkCompleted(String)
	 */
	public boolean checkIfAdvancementCompleted(final String path) throws InvalidAdvancementException {
		val progress = advancementProgress.get(path);
		if (progress == null) throw new InvalidAdvancementException();
		return progress.isCompleted();
	}

	/**
	 * Gets the progress this player made in a quest
	 *
	 * @param path the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return integer of the progression made
	 */
	public int getProgress(final String path) {
		return advancementProgress.get(path).getProgress();
	}

	/**
	 * Checks if a quest is completed and executes completion actions if completed. Does not return a boolean! Use {@link AdvancementProgress#isCompleted()} to get boolean if it's completed or not.
	 *
	 * @param path the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @see CAPlayer#checkIfAdvancementCompleted(String)
	 */
	public void checkCompleted(final String path) throws InvalidAdvancementException {
		val caProgress = advancementProgress.get(path);
		val progress = caProgress.getProgress();
		val maxProgress = CustomAdvancements.getAdvancementManager().getAdvancement(path).getMaxProgress();
		if (maxProgress <= progress) {
			caProgress.setCompleted(true);
			caProgress.setActive(false);
			CustomAdvancements.getAdvancementManager().complete(path, uuid);
		}
	}

	/**
	 * The amount of completed quests for this player
	 *
	 * @return integer of amount completed quests
	 */
	public int amountCompleted() {
		return amountCompleted(null);
	}

	/**
	 * Amount of completed quests of a specific tree for this player
	 *
	 * @param tree the label of the tree
	 * @return integer of amount completed quests in this tree
	 */
	public int amountCompleted(final String tree) {
		int result = 0;
		for (final Map.Entry<String, AdvancementProgress> entry : advancementProgress.entrySet()) {
			if (tree == null && entry.getValue().isCompleted()) {
				result++;
			} else if (entry.getKey().startsWith(tree + ".") && entry.getValue().isCompleted()) {
				result++;
			}
		}
		return result;
	}


	/**
	 * Attempts to activate an advancement if all requirements are met
	 *
	 * @param path the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return returns null if all requirements are met and is activated sucessfully, otherwise returns a list of all requirements that are not met
	 */
	public List<AdvancementRequirement> activateAdvancement(final String path) throws InvalidAdvancementException {
		return this.activateAdvancement(path, false);
	}

	/**
	 * Attempts to activate an advancement if all requirements are met (activates it anyways if param force is set to true)
	 *
	 * @param path  the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @param force should the advancement be activated even if the requirements are not met?
	 * @return returns null if all requirements are met and is activated sucessfully, otherwise returns a list of all requirements that are not met
	 */
	public List<AdvancementRequirement> activateAdvancement(final String path, final boolean force) throws InvalidAdvancementException {
		val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
		if (force || advancement.meetRequirements(Bukkit.getPlayer(this.uuid))) {
			advancementProgress.get(path).setActive(true);
			return null;
		} else {
			return advancement.getRequirements(false, Bukkit.getPlayer(this.uuid));
		}
	}
}
