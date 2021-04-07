package me.tippie.customadvancements.advancement.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class DistanceTravelled extends AdvancementType{
    DistanceTravelled(String label, String defaultUnit) {
        super(label, defaultUnit);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(Object event, String value, String path) {

    }
}
