package me.tippie.customadvancements.player;

import lombok.Getter;
import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.InternalsProvider;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import me.tippie.customadvancements.player.datafile.AdvancementProgress;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

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
	 * List of {@link me.tippie.customadvancements.advancement.reward.AdvancementReward}'s that has yet to be given to this player
	 */
	private Queue<AdvancementReward> pendingRewards = new LinkedList<>();

	/**
	 * List that contains recently visited GUI's, used for back button in inventory gui's
	 */
	@Getter private final LinkedList<String> guiHistory = new LinkedList<>();

	/**
	 * Creates a new {@link CAPlayer} and loads their progress.
	 *
	 * @param playeruuid the UUID of the player
	 */
	CAPlayer(final UUID playeruuid) {
		advancementProgressFile = new AdvancementProgressFile(playeruuid);
		advancementProgress = advancementProgressFile.loadFile();
		uuid = playeruuid;
		loadPendingRewards();
	}

	/**
	 * Updates progress for any {@link me.tippie.customadvancements.advancement.CAdvancement}
	 * for this player with a certain amount and optionally checks if it is completed right away.
	 *
	 * @param path             the path of the completed advancement formatted as 'treeLabel.advancementLabel'
	 * @param amount           amount of increasement or decreasement
	 * @param checkIfCompleted boolean if this advancement should be check completed after progress is set
	 * @param set              boolean if the amount should be added to the progress or the progress should be set to the amount
	 */
	public void updateProgress(final String path, final int amount, final boolean checkIfCompleted, final boolean set) throws InvalidAdvancementException {
		val progress = advancementProgress.get(path);
		if (set)
			progress.setProgress(amount);
		else
			progress.setProgress(progress.getProgress() + amount);

		if (checkIfCompleted) checkCompleted(path);
	}

	public void updateMinecraftGui(String path) throws InvalidAdvancementException {
		if (CustomAdvancements.getInternals() == null) return;
		CAdvancement advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
		if (advancement.isHidden()) return;
		Player player = Bukkit.getPlayer(uuid);
		if (player != null)
			CustomAdvancements.getInternals().updateAdvancement(player,advancement)
					.exceptionally(e -> {
						CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Could not update advancement " + path + " for " + player.getName()+ "!",e);
						return null;
					});
	}

	/**
	 * Sends the player associated to this CAPlayer the advancements of the Minecraft GUI. <br />
	 * <b>NOTE:</b> Only use this when the player joins, or after a reload. Otherwise use {@link CAPlayer#updateMinecraftGui(String)}
	 */
	public void sendMinecraftGUI(){
		InternalsProvider internals = CustomAdvancements.getInternals();
		Player player = Bukkit.getPlayer(uuid);
		if (internals != null && player != null) {
			internals.sendAdvancements(player, CustomAdvancements.getInstance().getConfig().getBoolean("remove-default-trees", true))
					.exceptionally(e -> {
						CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Could not send advancements to " + player.getName()+ "!",e);
						return null;
					});;
		}
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
		this.updateProgress(path, amount, checkIfCompleted, false);
	}


	/**
	 * Checks if quest is active for this player
	 *
	 * @param path the path of an advancement formatted as 'treeLabel.advancementLabel'
	 * @return boolean if the quest is active
	 */
	public boolean checkIfAdvancementActive(final String path) throws InvalidAdvancementException {
		val advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
		return (advancementProgress.get(path).isActive() && !advancementProgress.get(path).isCompleted()) || (!advancementProgress.get(path).isCompleted() && CustomAdvancements.getAdvancementManager().getAdvancementTree(path.split("\\.")[0]).getOptions().isAutoActive() && advancement.meetRequirements(Bukkit.getPlayer(this.uuid)));
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
	public int getProgress(final String path) throws InvalidAdvancementException {
		try {
			return advancementProgress.get(path).getProgress();
		} catch (NullPointerException e){
			throw new InvalidAdvancementException("There exists no advancement for path " + path);
		}
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
			updateMinecraftGui(path);
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
			if (!force) advancement.activate(Bukkit.getPlayer(this.uuid));
			advancementProgress.get(path).setActive(true);
			return null;
		} else {
			return advancement.getRequirements(false, Bukkit.getPlayer(this.uuid));
		}
	}

	/**
	 * Gets all active advancements for this {@link CAPlayer}
	 *
	 * @return a list of all active {@link CAdvancement}'s
	 */
	public List<CAdvancement> getActiveAdvancements() {
		val trees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
		final List<CAdvancement> result = new LinkedList<>();
		for (final AdvancementTree tree : trees) {
			try {
				result.addAll(getActiveAdvancements(tree.getLabel()));
			} catch (final InvalidAdvancementException ignored) {

			}
		}
		return result;
	}

	/**
	 * Gets all active advancements of a specific tree
	 *
	 * @param treeLabel The label of the tree the active advancements should be get for
	 * @return a list of all active {@link CAdvancement}'s
	 * @throws InvalidAdvancementException when the given label is not a valid tree
	 */
	public List<CAdvancement> getActiveAdvancements(final String treeLabel) throws InvalidAdvancementException {
		val tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(treeLabel);
		final List<CAdvancement> result = new LinkedList<>();
		for (final CAdvancement advancement : tree.getAdvancements()) {
			if (checkIfAdvancementActive(tree.getLabel() + "." + advancement.getLabel())) {
				result.add(advancement);
			}
		}
		return result;
	}

	/**
	 * Gets all completed advancements for this {@link CAPlayer}
	 *
	 * @return a list of all completed {@link CAdvancement}'s
	 */
	public List<CAdvancement> getCompletedAdvancements() {
		val trees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
		final List<CAdvancement> result = new LinkedList<>();
		for (final AdvancementTree tree : trees) {
			try {
				result.addAll(getCompletedAdvancements(tree.getLabel()));
			} catch (final InvalidAdvancementException ignored) {

			}
		}
		return result;
	}

	/**
	 * Gets all completed advancements of a specific tree
	 *
	 * @param treeLabel The label of the tree the completed advancements should be get for
	 * @return a list of all completed {@link CAdvancement}'s
	 * @throws InvalidAdvancementException when the given label is not a valid tree
	 */
	public List<CAdvancement> getCompletedAdvancements(final String treeLabel) throws InvalidAdvancementException {
		val tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(treeLabel);
		final List<CAdvancement> result = new LinkedList<>();
		for (final CAdvancement advancement : tree.getAdvancements()) {
			if (checkIfAdvancementCompleted(advancement.getPath())) {
				result.add(advancement);
			}
		}
		return result;
	}

	/**
	 * Gets all advancements that can be activated for this {@link CAPlayer}
	 *
	 * @return a list of all available {@link CAdvancement}'s
	 */
	public List<CAdvancement> getAvailableAdvancements() {
		val trees = CustomAdvancements.getAdvancementManager().getAdvancementTrees();
		final List<CAdvancement> result = new LinkedList<>();
		for (final AdvancementTree tree : trees) {
			try {
				result.addAll(getAvailableAdvancements(tree.getLabel()));
			} catch (final InvalidAdvancementException ignored) {

			}
		}
		return result;
	}

	/**
	 * Gets all advancements that can be activated of a specific tree
	 *
	 * @param treeLabel The label of the tree the available advancements should be get for
	 * @return a list of all available {@link CAdvancement}'s
	 * @throws InvalidAdvancementException when the given label is not a valid tree
	 */
	public List<CAdvancement> getAvailableAdvancements(final String treeLabel) throws InvalidAdvancementException {
		val tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(treeLabel);
		final List<CAdvancement> result = new LinkedList<>();
		for (final CAdvancement advancement : tree.getAdvancements()) {
			if (advancement.meetRequirements(Bukkit.getPlayer(this.uuid)) && !checkIfAdvancementActive(advancement.getPath()) && !checkIfAdvancementCompleted(advancement.getPath())) {
				result.add(advancement);
			}
		}
		return result;
	}

	/**
	 * Adds a pending reward for this player
	 *
	 * @param reward the reward that should be added
	 */
	public void addPendingReward(final AdvancementReward reward) {
		pendingRewards.add(reward);
	}

	/**
	 * Attempt to give this player all pending rewards
	 */
	public void givePendingRewards() {
		final Player player = Bukkit.getPlayer(this.uuid);
		if (player == null || !player.isOnline()) return;
		for (AdvancementReward reward; (reward = pendingRewards.poll()) != null; ) {
			reward.onComplete(player);
		}
	}

	public void loadPendingRewards() {
		final File file = new File(CustomAdvancements.getInstance().getDataFolder() + "/data/pendingrewards.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException ex) {
				CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
			}
		}
		final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		try {
			data.load(file);
			final Queue<AdvancementReward> pending = new LinkedList<>();
			if (data.getConfigurationSection(String.valueOf(this.uuid)) != null) {
				val pendingList = data.getConfigurationSection(String.valueOf(this.uuid));
				if (pendingList != null) {
					for (final String i : pendingList.getKeys(false)) {
						val type = pendingList.getString(i + ".type");
						val value = pendingList.getString(i + ".value");
						pending.add(new AdvancementReward(type, value));
					}
				}
			}
			pendingRewards = pending;
		} catch (final Exception ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to load pending rewards!", ex);
		}
	}

	public void savePendingRewards() {
		final File file = new File(CustomAdvancements.getInstance().getDataFolder() + "/data/pendingrewards.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException ex) {
				CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to read and/or create plugin directory.", ex);
			}
		}
		final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		try {
			data.load(file);
			if (data.getConfigurationSection(String.valueOf(this.uuid)) != null) {
				data.set(String.valueOf(this.uuid), null);
			}
			final int i = 0;
			for (final AdvancementReward reward : pendingRewards) {
				data.set(this.uuid + "." + i + ".type", reward.getType().getLabel());
				data.set(this.uuid + "." + i + ".value", reward.getValue());
			}
			data.save(file);
		} catch (final Exception ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to save pending rewards!", ex);
		}
	}
}
