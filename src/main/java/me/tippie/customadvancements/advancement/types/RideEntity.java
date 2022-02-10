package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.*;

public class RideEntity extends AdvancementType{
    public RideEntity() {
        super("rideentity", "blocks");
        Bukkit.getScheduler().runTaskTimer(CustomAdvancements.getInstance(),() -> {
           for (Map.Entry<Entity, Map.Entry<Player, Location>> entry : ((HashMap<Entity, Map.Entry<Player, Location>>) mountedEntities.clone()).entrySet()){
                if (!entry.getKey().getPassengers().contains(entry.getValue().getKey())) mountedEntities.remove(entry.getKey());
                progress(entry,entry.getValue().getKey().getUniqueId());
           }
        },10L,10L);
    }

    private final HashMap<Entity, Map.Entry<Player, Location>> mountedEntities = new HashMap<>();

    @EventHandler
    private void onRide(EntityMountEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        mountedEntities.put(event.getMount(),new AbstractMap.SimpleEntry<>((Player) event.getEntity(), event.getEntity().getLocation()));
    }

    @Override
    protected void onProgress(Object e, String value, String path) {
        Map.Entry<Entity, Map.Entry<Player, Location>> entry = (Map.Entry<Entity, Map.Entry<Player, Location>>) e;
        Location oldLoc = entry.getValue().getValue();
        Location newLoc = entry.getValue().getKey().getLocation();
        UUID uuid = entry.getValue().getKey().getUniqueId();
        EntityType type = entry.getKey().getType();
        entry.getValue().setValue(newLoc);
        int distance = Math.abs(oldLoc.getBlockX() - newLoc.getBlockX()) + Math.abs(oldLoc.getBlockZ() - newLoc.getBlockZ());
        if (distance < 1) return;
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(distance, path, uuid);
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
            if ((entities.contains(type) && !not) || (!entities.contains(type) && not)) {
                progression(distance, path, uuid);
            }
        }
    }
}
