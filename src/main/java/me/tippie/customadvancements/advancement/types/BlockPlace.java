package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockPlace extends AdvancementType {
	public BlockPlace() {
		super("blockplace", Lang.ADVANCEMENT_TYPE_BLOCKPLACE_UNIT.getString());
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, String value, final String path) {
		val blockPlaceEvent = (BlockPlaceEvent) event;
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, blockPlaceEvent.getPlayer().getUniqueId());
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
			if ((materials.contains(blockPlaceEvent.getBlock().getType()) && !not) || (!materials.contains(blockPlaceEvent.getBlock().getType()) && not)) {
				progression(1, path, blockPlaceEvent.getPlayer().getUniqueId());
			}
		}
	}
}
