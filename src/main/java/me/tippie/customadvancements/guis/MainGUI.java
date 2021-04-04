package me.tippie.customadvancements.guis;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainGUI extends InventoryGUI {

	public MainGUI() {
		super(27, Lang.GUI_MAIN_TITLE.getString());
	}

	@Override public Inventory getInventory(final Player player) {
		final ItemStack advancementTreeItem = createGuiItem(Material.OAK_SAPLING, Lang.GUI_MAIN_TREES_NAME.getString(), Lang.GUI_MAIN_TREES_LORE.getString());
		inventory.setItem(10, advancementTreeItem);
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		switch (index) {
			case 10:
				event.getWhoClicked().openInventory(new TreeGUI(1).getInventory((Player) event.getWhoClicked()));
		}
	}

}
