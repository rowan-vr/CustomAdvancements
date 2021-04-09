package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.LinkedList;
import java.util.List;

public abstract class InventoryGUI implements Listener {

	protected final Inventory inventory;

	InventoryGUI(final int size, final String name) {
		inventory = Bukkit.createInventory(null, size, name);
		CustomAdvancements.getInstance().getServer().getPluginManager().registerEvents(this, CustomAdvancements.getInstance());
	}


	public abstract Inventory getInventory(Player player) throws InvalidAdvancementException;

	public abstract void onClick(InventoryClickEvent event);


	protected ItemStack createGuiItem(final Material material, final String name, final String lore) {
		final ItemStack item = new ItemStack(material, 1);
		return createGuiItem(item, name, lore);
	}

	protected ItemStack createGuiItem(final ItemStack itemIn, final String name, final String lore) {
		final ItemStack item = itemIn.clone();
		final ItemMeta meta = item.getItemMeta();

		// Set the name of the item
		assert meta != null;
		meta.setDisplayName(ChatColor.WHITE + name);

		// Set the lore of the item
		if (lore != null) {
			List<String> loreLines = new LinkedList<>();
			Arrays.asList(lore.split("\\\\n")).forEach(line -> loreLines.add(ChatColor.GRAY + line));
			meta.setLore(loreLines);
		}

		item.setItemMeta(meta);

		return item;
	}

	protected void setPaging(int page, int maxPage) {
		if (maxPage != 1) {
			val backItem = !(page == 1) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_PREVIOUS_NAME.getString(), Lang.GUI_PAGE_PREVIOUS_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_FIRST_NAME.getString(), Lang.GUI_PAGE_FIRST_LORE.getString());
			val nextItem = !(page == maxPage) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_NEXT_NAME.getString(), Lang.GUI_PAGE_NEXT_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_LAST_NAME.getString(), Lang.GUI_PAGE_LAST_LORE.getString());
			inventory.setItem(18, backItem);
			inventory.setItem(26, nextItem);
		}
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
			HandlerList.unregisterAll(this);
		}
	}
}
