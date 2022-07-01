package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.advancement.CAdvancement;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the blockbreak {@link AdvancementType}
 * This type gets counted when a block is broken.
 */
public class BlockBreak extends AdvancementType<BlockBreakEvent> {

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

	/**
	 * Called when progress is made
	 *
	 * @param event The event what progress is made on
	 * @param value The value of the {@link CAdvancement}
	 * @param path  The path of the {@link CAdvancement}
	 */
	@Override
	protected void onProgress(final BlockBreakEvent event, String value, final String path) {
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, event.getPlayer().getUniqueId());
		} else {
			boolean not = false;
			if (value.startsWith("!")) {
				value = value.substring(1);
				not = true;
			}
			final List<Material> materials = new ArrayList<>();
			final String[] materialStrings = value.split(",");
			for (final String materialString : materialStrings)
				materials.add(Material.getMaterial(materialString.toUpperCase()));
			if ((materials.contains(event.getBlock().getType()) && !not) || (!materials.contains(event.getBlock().getType()) && not)) {
				progression(1, path, event.getPlayer().getUniqueId());
			}
		}
	}
}
