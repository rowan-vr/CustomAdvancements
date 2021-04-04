package me.tippie.customadvancements.guis;

import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class InventoryGUI implements Listener {

	protected final Inventory inventory;

	InventoryGUI(final int size, final String name) {
		inventory = Bukkit.createInventory(null, size, name);
		CustomAdvancements.getInstance().getServer().getPluginManager().registerEvents(this, CustomAdvancements.getInstance());
		System.out.println("registered");
	}


	public abstract Inventory getInventory(Player player);

	public abstract void onClick(InventoryClickEvent event);

	protected ItemStack createGuiItem(final Material material, final String name, final String lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();

		// Set the name of the item
		assert meta != null;
		meta.setDisplayName(name);

		// Set the lore of the item
		if (lore != null) {
			meta.setLore(Arrays.asList(lore.split("\\\\n")));
		}

		item.setItemMeta(meta);

		return item;
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (event.getInventory().equals(inventory)) {
			event.setCancelled(true);
			onClick(event);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onClose(final InventoryCloseEvent event) {
		if (event.getInventory().equals(inventory)) {
			System.out.println("unregistered");
			HandlerList.unregisterAll(this);
		}
	}
}
