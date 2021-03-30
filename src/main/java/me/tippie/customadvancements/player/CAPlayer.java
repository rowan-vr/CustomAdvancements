package me.tippie.customadvancements.player;

import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.player.datafile.AdvancementProgress;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;

import java.util.Map;
import java.util.UUID;

public class CAPlayer {
	private UUID uuid;
	private Map<String, AdvancementProgress> advancementProgress;

	CAPlayer(UUID playeruuid){
		AdvancementProgressFile advancementProgressFile = new AdvancementProgressFile(playeruuid);
		advancementProgressFile.loadFile();
		advancementProgress = advancementProgressFile.getAdvancementProgress();
		uuid = playeruuid;
	}
}
