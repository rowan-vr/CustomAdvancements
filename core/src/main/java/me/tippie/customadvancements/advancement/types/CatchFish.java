package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CatchFish extends AdvancementType<PlayerFishEvent> {
	public CatchFish() {
		super("catchfish", Lang.ADVANCEMENT_TYPE_CATCHFISH_UNIT.getString());
	}

	@EventHandler
	public void onFishEvent(final PlayerFishEvent event) {
		if (event.getCaught() != null && event.getCaught() instanceof Item)
			progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final PlayerFishEvent event, String value, final String path) {
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
			if ((materials.contains(((Item) Objects.requireNonNull(event.getCaught())).getItemStack().getType()) && !not) || (!materials.contains(((Item) Objects.requireNonNull(event.getCaught())).getItemStack().getType()) && not)) {
				progression(1, path, event.getPlayer().getUniqueId());
			}
		}
	}
}
