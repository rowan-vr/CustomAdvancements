package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class ShearSheep extends AdvancementType {
    public ShearSheep() {
        super("shearsheep", Lang.ADVANCEMENT_TYPE_SHEARSHEEP_UNIT.getString());
    }

    @EventHandler
    public void onShear(final PlayerShearEntityEvent event) {
        if (event.getEntity() instanceof Sheep) {
            progress(event, event.getPlayer().getUniqueId());
        }
    }

    @Override protected void onProgress(final Object event, String value, final String path) {
        val playerShearEntityEvent = (PlayerShearEntityEvent) event;
        val player = playerShearEntityEvent.getPlayer();
        val sheep = (Sheep) playerShearEntityEvent.getEntity();
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(1, path, player.getUniqueId());
        } else {
            boolean not = false;
            if (value.startsWith("!")) {
                value = value.substring(1);
                not = true;
            }
            final List<DyeColor> colors = new ArrayList<>();
            final String[] colorStrings = value.split(",");
            for (final String colorString : colorStrings)
                colors.add(DyeColor.valueOf(colorString.toUpperCase()));
            if ((colors.contains(sheep.getColor()) && !not) || (!colors.contains(sheep.getColor()) && not)) {
                progression(1, path, player.getUniqueId());
            }
        }
    }
}
