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
import java.util.logging.Level;
import java.util.stream.Stream;

public abstract class InventoryGUI implements Listener {

	protected final Inventory inventory;
	private InventoryGUI lastMenu;
	private int backSlot;

	InventoryGUI(final int size, final String name) {
		inventory = Bukkit.createInventory(null, size, name);
		CustomAdvancements.getInstance().getServer().getPluginManager().registerEvents(this, CustomAdvancements.getInstance());
	}

	public Inventory getInventory(Player player) throws InvalidAdvancementException {
		return getInventory(player,false);
	}

	public abstract Inventory getInventory(Player player, boolean ignoreHistory) throws InvalidAdvancementException;

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

	protected void setBack(int slot, boolean ignoreLast){
		if (lastMenu != null) {
			backSlot = slot;
			val backItem = createGuiItem(Material.DARK_OAK_DOOR, Lang.GUI_BACK_BUTTON_NAME.getString(), Lang.GUI_BACK_BUTTON_LORE.getString());
			inventory.setItem(slot,backItem);
		}
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
		if (event.getRawSlot() == backSlot) {

		} else if (event.getInventory().equals(inventory)) {
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

	public static void openInventoryByString(String string, Player player) throws InvalidAdvancementException {
		val args = string.split(":");
		val name = args[0].toLowerCase();
		int page;
		String path, tree;
		switch (name) {
			case "main":
				player.openInventory(new MainGUI().getInventory(player));
				return;
			case "tree":
				page = Integer.parseInt(args[1]);
				player.openInventory(new TreeGUI(page).getInventory(player));
				return;
			case "activeadvancements":
				page = Integer.parseInt(args[1]);
				player.openInventory(new ActiveAdvancementsGUI(page, player).getInventory(player));
				return;
			case "advancementsoptions":
				path = args[1];
				player.openInventory(new AdvancementOptionsGUI(path).getInventory(player));
				return;
			case "advancements":
				tree = args[1];
				page = Integer.parseInt(args[2]);
				player.openInventory(new AdvancementsGUI(tree, page).getInventory(player));
				return;
			case "availableadvancements":
				page = Integer.parseInt(args[1]);
				player.openInventory(new AvailableAdvancementsGUI(page, player).getInventory(player));
				return;
			case "requirements":
				path = args[1];
				page = Integer.parseInt(args[2]);
				player.openInventory(new RequirementsGUI(path, page, player).getInventory(player));
				return;
			default:
				CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Error during openInventoryByString().. Invalid string provided.");
		}
	}

	protected <T> Stream<T> replaceLast(List<T> items, T last) {
		Stream<T> allExceptLast = items.stream().limit(items.size() - 1);
		return Stream.concat(allExceptLast, Stream.of(last));
	}
}
