package me.tippie.customadvancements.advancement.types;

import lombok.val;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.ArrayList;
import java.util.List;

public class Taming extends AdvancementType<EntityTameEvent> {
    public Taming() {
        super("taming", "animals");
    }

    @EventHandler
    public void onTame(EntityTameEvent event){
        progress(event, event.getOwner().getUniqueId());
    }

    @Override
    protected void onProgress(EntityTameEvent event, String value, String path) {
        val player = event.getOwner();
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(1, path, player.getUniqueId());
        } else {
            boolean not = false;
            if (value.startsWith("!")) {
                value = value.substring(1);
                not = true;
            }
            final List<EntityType> entities = new ArrayList<>();
            final String[] entityStrings = value.split(",");
            for (final String causeString : entityStrings)
                entities.add(EntityType.valueOf(causeString.toUpperCase()));
            if ((entities.contains(event.getEntityType()) && !not) || (!entities.contains(event.getEntityType()) && not)) {
                progression(1, path, player.getUniqueId());
            }
        }
    }
}
