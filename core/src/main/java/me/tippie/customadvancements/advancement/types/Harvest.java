package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Harvest extends AdvancementType<PlayerHarvestBlockEvent> {
    public Harvest() {
        super("harvest", Lang.ADVANCEMENT_TYPE_CRAFTITEM_UNIT.getString());
    }

    @EventHandler
    public void onBlockHavervest(final PlayerHarvestBlockEvent event) {
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override protected void onProgress(final PlayerHarvestBlockEvent event, String value, final String path) {
        val player = event.getPlayer();
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(1, path, player.getUniqueId());
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
            final List<Material> harvested = new ArrayList<>();
            for (ItemStack item : event.getItemsHarvested()){
                harvested.add(item.getType());
            }
            if ((Collections.disjoint(materials,harvested) && not) || (!Collections.disjoint(materials,harvested) && !not)) {
                progression(1, path, player.getUniqueId());
            }
        }
    }
}
