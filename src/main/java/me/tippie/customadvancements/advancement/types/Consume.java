package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.ArrayList;
import java.util.List;

public class Consume extends AdvancementType {

	public Consume() {
		super("consume", Lang.ADVANCEMENT_TYPE_CONSUME_UNIT.getString());
	}

	@EventHandler
	public void onConsume(final PlayerItemConsumeEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, String value, final String path) {
		final PlayerItemConsumeEvent playerItemConsumeEvent = (PlayerItemConsumeEvent) event;
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, playerItemConsumeEvent.getPlayer().getUniqueId());
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
			if ((materials.contains(playerItemConsumeEvent.getItem().getType()) && !not) || (!materials.contains(playerItemConsumeEvent.getItem().getType()) && not)) {
				progression(1, path, playerItemConsumeEvent.getPlayer().getUniqueId());
			}
		}
	}
}
