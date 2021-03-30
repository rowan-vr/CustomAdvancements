package me.tippie.customadvancements.advancement.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class BlockBreak extends AdvancementType {

	public BlockBreak() {
		super("BlockBreak");
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		final UUID playeruuid = event.getPlayer().getUniqueId();
		progress(1, playeruuid);
	}

	@Override
	public void onProgress() {

	}
}
