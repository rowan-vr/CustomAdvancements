package me.tippie.customadvancements.player.datafile;

import me.tippie.customadvancements.CustomAdvancements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdvancementProgressFile {

	private final Map<String, AdvancementProgress> advancementProgress = new HashMap();
	private final CustomAdvancements plugin;
	private final UUID playeruuid;

	AdvancementProgressFile(final CustomAdvancements plugin, final UUID playeruuid) {
		this.plugin = plugin;
		this.playeruuid = playeruuid;
	}
}
