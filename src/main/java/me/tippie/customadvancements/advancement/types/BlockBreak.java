package me.tippie.customadvancements.advancement.types;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

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
		super("BlockBreak", "blocks broken");
	}

	/**
	 * Registers 1 progress when a block is broken.
	 *
	 * @param event BlockBreakEvent
	 */
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override
	protected void onProgress(final Object event, final String value, final String path) {
		val blockBreakEvent = (BlockBreakEvent) event;
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, blockBreakEvent.getPlayer().getUniqueId());
		} else {
			final List<Material> materials = new ArrayList<>();
			final String[] materialStrings = value.split(",");
			for (final String materialString : materialStrings) materials.add(Material.getMaterial(materialString));
			if (materials.contains(blockBreakEvent.getBlock().getType())) {
				progression(1, path, blockBreakEvent.getPlayer().getUniqueId());
			}
		}
	}
}
