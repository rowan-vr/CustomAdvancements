package me.tippie.customadvancements.advancement.types;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class Enchant extends AdvancementType<EnchantItemEvent> {

    public Enchant() {
        super("enchant", "items");
    }

    @EventHandler
    private void onEnchant(EnchantItemEvent event){
        progress(event,event.getEnchanter().getUniqueId());
    }

    @Override
    protected void onProgress(EnchantItemEvent e, String value, String path) {
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(1, path, e.getEnchanter().getUniqueId());
        } else {
            boolean not = false;
            if (value.startsWith("!")) {
                value = value.substring(1);
                not = true;
            }
            final String[] enchantsString = value.split(",");
            for (final String enchantString : enchantsString) {
                Enchantment enchant = Enchantment.getByName(enchantString.split("-")[0]);
                int level = Integer.parseInt(enchantString.split("-")[1]);
                if (e.getEnchantsToAdd().get(enchant) != null && e.getEnchantsToAdd().get(enchant) == level){
                    progression(1,path, e.getEnchanter().getUniqueId());
                    return;
                }
            }
        }
    }
}
