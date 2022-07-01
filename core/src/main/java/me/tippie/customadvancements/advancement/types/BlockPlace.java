package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockPlace extends AdvancementType<BlockPlaceEvent> {
	public BlockPlace() {
		super("blockplace", Lang.ADVANCEMENT_TYPE_BLOCKPLACE_UNIT.getString());
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final BlockPlaceEvent event, String value, final String path) {
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
