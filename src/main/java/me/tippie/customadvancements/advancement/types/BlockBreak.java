package me.tippie.customadvancements.advancement.types;

import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak extends AdvancementType {

	public BlockBreak() {
		super("BlockBreak");
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		val playeruuid = event.getPlayer().getUniqueId();
		progress(1, playeruuid);
	}

	@Override
	public void onProgress() {

	}
}
