package me.tippie.customadvancements.advancement.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import java.util.ArrayList;
import java.util.List;

public class Smelting extends AdvancementType {
    public Smelting() {
        super("smelting", "blocks");
    }

    @EventHandler
    public void onCraft(FurnaceExtractEvent event) {
        progress(event,event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(Object e, String value, String path) {
        FurnaceExtractEvent event = (FurnaceExtractEvent) e;
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(event.getItemAmount(), path, event.getPlayer().getUniqueId());
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
            if ((materials.contains(event.getItemType()) && !not) || (!materials.contains(event.getItemType()) && not)) {
                progression(event.getItemAmount(), path, event.getPlayer().getUniqueId());
            }
        }
    }
}
