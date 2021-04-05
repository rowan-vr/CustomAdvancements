package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CatchFish extends AdvancementType {
	public CatchFish() {
		super("catchfish", Lang.ADVANCEMENT_TYPE_CATCHFISH_UNIT.getString());
	}

	@EventHandler
	public void onFishEvent(final PlayerFishEvent event) {
		if (event.getCaught() != null && event.getCaught() instanceof Item)
			progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, final String value, final String path) {
		final PlayerFishEvent playerFishEvent = (PlayerFishEvent) event;
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, playerFishEvent.getPlayer().getUniqueId());
		} else {
			final List<Material> materials = new ArrayList<>();
			final String[] materialStrings = value.split(",");
			for (final String materialString : materialStrings)
				materials.add(Material.getMaterial(materialString.toUpperCase()));
			if (materials.contains(((Item) Objects.requireNonNull(playerFishEvent.getCaught())).getItemStack().getType())) {
				progression(1, path, playerFishEvent.getPlayer().getUniqueId());
			}
		}
	}
}
