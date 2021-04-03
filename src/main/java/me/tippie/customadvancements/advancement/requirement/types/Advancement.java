package me.tippie.customadvancements.advancement.requirement.types;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.entity.Player;

public class Advancement extends AdvancementRequirementType {
	public Advancement() {
		super("advancement");
	}

	@Override public boolean isMet(final String path, final Player player) {
		val caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());
		return caPlayer.checkIfQuestCompleted(path);
	}

	@Override public String getNotMetMessage(final String path, final Player player) {
		val advancement = path.split("\\.")[1];
		val tree = path.split("\\.")[0];
		return Lang.REQUIREMENT_ADVANCEMENT_NOTMET.getConfigValue(new String[]{advancement, tree}, true);
	}
}
