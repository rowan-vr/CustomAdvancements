package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageTaken extends AdvancementType {
	public DamageTaken() {
		super("damagetaken", Lang.ADVANCEMENT_TYPE_DAMAGETAKEN_UNIT.getString());
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			progress(event, Objects.requireNonNull(((Player) event.getEntity()).getPlayer()).getUniqueId());
		}
	}

	@Override protected void onProgress(final Object event, String value, final String path) {
		val entityDamageEvent = (EntityDamageEvent) event;
		val player = (Player) entityDamageEvent.getEntity();
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, player.getUniqueId());
		} else {
			boolean not = false;
			if (value.startsWith("!")) {
				value = value.substring(1);
				not = true;
			}
			final List<EntityDamageEvent.DamageCause> causes = new ArrayList<>();
			final String[] causeStrings = value.split(",");
			for (final String causeString : causeStrings)
				causes.add(EntityDamageEvent.DamageCause.valueOf(causeString.toUpperCase()));
			if ((causes.contains(entityDamageEvent.getCause()) && !not) || (!causes.contains(entityDamageEvent.getCause()) && not)) {
				progression(1, path, player.getUniqueId());
			}
		}
	}
}

