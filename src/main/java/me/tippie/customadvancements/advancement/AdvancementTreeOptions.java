package me.tippie.customadvancements.advancement;

import lombok.Getter;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents the options of an AdvancementTree
 *
 * @see AdvancementTree
 */
@Getter
public class AdvancementTreeOptions {

	/**
	 * If all quests are always active or need to be unlocked
	 */
	private final boolean allActive;

	/**
	 * Should advancements where the requirements are met automatically be activated
	 */
	private final boolean autoActive;

	/**
	 * Type of reward this tree would grant the user on full completion
	 */
	private final List<AdvancementReward> rewards;


	public AdvancementTreeOptions(final boolean allActive, final boolean autoActive, final List<AdvancementReward> rewards) {
		this.allActive = allActive;
		this.autoActive = autoActive;
		this.rewards = rewards;
	}

	public void onComplete(final Player player) {
		for (final AdvancementReward reward : rewards) {
			reward.onComplete(player);
		}
	}
}
