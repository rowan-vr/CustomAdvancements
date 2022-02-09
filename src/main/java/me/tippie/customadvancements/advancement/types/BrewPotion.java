package me.tippie.customadvancements.advancement.types;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class BrewPotion extends AdvancementType{

    public BrewPotion() {
        super("brew", "potions");
    }

    private final HashMap<ItemStack, UUID> itemTrackMap = new HashMap<>();

    @EventHandler
    public void onInventoryPut(InventoryMoveItemEvent event){
        if (event.getDestination() instanceof BrewerInventory && event.getSource() instanceof PlayerInventory) {
            itemTrackMap.put(event.getItem(),((Player) event.getSource().getHolder()).getUniqueId());
        } else if (!(event.getDestination() instanceof BrewerInventory) && event.getSource() instanceof BrewerInventory) {
            itemTrackMap.remove(event.getItem());
        }
    }

    @EventHandler
    public void onBrew(BrewEvent e){
        for (int i = 0; i < 3; i++){
            ItemStack potion = e.getContents().getItem(i);
            UUID uuid = itemTrackMap.get(potion);
            if (uuid != null)
                progress(potion,uuid);
        }
    }

    @Override
    protected void onProgress(Object e, String value, String path) {
        ItemStack potion = (ItemStack) e;
        UUID uuid = itemTrackMap.remove(potion);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        Set<PotionEffectType> effects = meta.getCustomEffects().stream().map(PotionEffect::getType).collect(Collectors.toSet());
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(1,path,uuid);
        } else {
            boolean not = false;
            if (value.startsWith("!")) {
                value = value.substring(1);
                not = true;
            }
            final List<PotionEffectType> types = new ArrayList<>();
            final String[] typeStrings = value.split(",");
            for (final String typeString : typeStrings)
                types.add(PotionEffectType.getByName(typeString.toUpperCase()));

            for (PotionEffectType type: effects) {
                if ((types.contains(type) && !not) || (!types.contains(type) && not)) {
                    progression(1, path, uuid);
                }
            }
        }
    }
}
