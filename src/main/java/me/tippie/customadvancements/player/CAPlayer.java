package me.tippie.customadvancements.player;

import lombok.Getter;
import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.player.datafile.AdvancementProgress;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CAPlayer {
	private final UUID uuid;
	@Getter
	private final Map<String, AdvancementProgress> advancementProgress;
	@Getter
	private final AdvancementProgressFile advancementProgressFile;

	CAPlayer(final UUID playeruuid) {
		advancementProgressFile = new AdvancementProgressFile(playeruuid);
		advancementProgress = advancementProgressFile.loadFile();
		uuid = playeruuid;
	}

	public void updateProgress(final String path, final int amount, final boolean checkIfCompleted) {
		val progress = advancementProgress.get(path);
		progress.setProgress(progress.getProgress() + amount);
		if (checkIfCompleted) checkCompleted(path);
	}

	public boolean checkIfQuestActive(final String path) {
		return advancementProgress.get(path).isActive();
	}

	public void checkCompleted(final String path) {
		val caProgress = advancementProgress.get(path);
		val progress = caProgress.getProgress();
		val maxProgress = CustomAdvancements.getAdvancementManager().getAdvancement(path).getMaxProgress();
		Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage("Checking " + path + " with progress/maxprogress: " + progress + "/" + maxProgress);
		if (maxProgress <= progress) {
			CustomAdvancements.getAdvancementManager().complete(path, uuid);
			caProgress.setCompleted(true);
			caProgress.setActive(false);
		}
	}
}
