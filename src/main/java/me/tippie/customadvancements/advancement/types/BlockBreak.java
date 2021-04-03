package me.tippie.customadvancements.advancement.types;

import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Represents the blockbreak {@link AdvancementType}
 * This type gets counted when a block is broken.
 */
public class BlockBreak extends AdvancementType {

	/**
	 * Creates a new BlockBreak {@link AdvancementType}
	 *
	 * @see AdvancementType
	 */
	public BlockBreak() {
		super("BlockBreak");
	}

	/**
	 * Registers 1 progress when a block is broken.
	 *
	 * @param event BlockBreakEvent
	 */
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		val playeruuid = event.getPlayer().getUniqueId();
		progress(1, playeruuid);
		System.out.println("block broken!");
	}
}
