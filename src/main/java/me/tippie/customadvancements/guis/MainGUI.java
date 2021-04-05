package me.tippie.customadvancements.guis;

import lombok.val;
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

		final ItemStack activeAdvancementsItem = createGuiItem(Material.ENCHANTED_BOOK, Lang.GUI_MAIN_ACTIVE_NAME.getString(), Lang.GUI_MAIN_ACTIVE_LORE.getString());
		inventory.setItem(12,activeAdvancementsItem);

		final ItemStack completedAdvancementsItem = createGuiItem(Material.GREEN_WOOL, Lang.GUI_MAIN_COMPLETED_NAME.getString(), Lang.GUI_MAIN_COMPLETED_LORE.getString());
		inventory.setItem(14,completedAdvancementsItem);

		final ItemStack availableAdvancementsItem = createGuiItem(Material.GREEN_STAINED_GLASS, Lang.GUI_MAIN_AVAILABLE_NAME.getString(), Lang.GUI_MAIN_AVAILABLE_LORE.getString());
		inventory.setItem(16,availableAdvancementsItem);
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		val player = (Player) event.getWhoClicked();
		switch (index) {
			case 10:
				player.openInventory(new TreeGUI(1).getInventory(player));
			case 12:
				player.openInventory(new ActiveAdvancementsGUI(1, player).getInventory(player));
			case 14:
				player.openInventory(new CompletedAdvancementsGUI(1, player).getInventory(player));
			case 16:
				player.openInventory(new AvailableAdvancementsGUI(1, player).getInventory(player));
		}
	}

}