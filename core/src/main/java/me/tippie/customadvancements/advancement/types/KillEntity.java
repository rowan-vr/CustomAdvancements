package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class KillEntity extends AdvancementType{

    public KillEntity() {
        super("killentity", Lang.ADVANCEMENT_TYPE_KILLENTITY_UNIT.getString());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        Player killer = event.getEntity().getKiller();
        if (killer != null){
            progress(event, killer.getUniqueId());
        }
    }

    @Override protected void onProgress(final Object event, String value, final String path) {
        val enityDeathEvent = (EntityDeathEvent) event;
        val player = enityDeathEvent.getEntity().getKiller();
        if (player == null) return;
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
            if ((entities.contains(enityDeathEvent.getEntityType()) && !not) || (!entities.contains(enityDeathEvent.getEntityType()) && not)) {
                progression(1, path, player.getUniqueId());
            }
        }
    }
}
