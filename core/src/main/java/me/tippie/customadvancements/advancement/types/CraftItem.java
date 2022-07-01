package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;
import java.util.List;

public class CraftItem extends AdvancementType<CraftItemEvent> {
	public CraftItem() {
		super("craftitem", Lang.ADVANCEMENT_TYPE_CRAFTITEM_UNIT.getString());
	}

	@EventHandler
	public void onBlockPlace(final CraftItemEvent event) {
		progress(event, event.getView().getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final CraftItemEvent event, String value, final String path) {
		val player = event.getView().getPlayer();
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, player.getUniqueId());
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
			if ((materials.contains(event.getRecipe().getResult().getType()) && !not) || (!materials.contains(event.getRecipe().getResult().getType()) && not)) {
				progression(1, path, player.getUniqueId());
			}
		}
	}
}
