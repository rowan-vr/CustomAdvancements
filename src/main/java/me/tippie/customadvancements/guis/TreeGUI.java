package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeGUI extends InventoryGUI {
	private final int page;

	TreeGUI(final int page) {
		super(27, Lang.GUI_TREES_TITLE.getConfigValue(new String[]{String.valueOf(page), String.valueOf(getMaxPage())}, true));
		this.page = page;
	}

	@Override public Inventory getInventory(final Player player) {
		for (final AdvancementTree tree : CustomAdvancements.getAdvancementManager().getAdvancementTrees()) {
			int page, index;
			try {
				page = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[0]);
				index = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[1]);
			} catch (final NumberFormatException ex) {
				page = 1;
				index = 0;
			}
			if (page == this.page) {
				val item = createGuiItem(Material.OAK_SAPLING, tree.getLabel(), "TBD");
				inventory.setItem(index, item);
			}
			val backItem = !(this.page == 1) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_PREVIOUS_NAME.getString(), Lang.GUI_PAGE_PREVIOUS_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_FIRST_NAME.getString(), Lang.GUI_PAGE_FIRST_LORE.getString());
			val nextItem = !(this.page == getMaxPage()) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_NEXT_NAME.getString(), Lang.GUI_PAGE_NEXT_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_LAST_NAME.getString(), Lang.GUI_PAGE_LAST_LORE.getString());
			inventory.setItem(18, backItem);
			inventory.setItem(26, nextItem);
		}
		return inventory;
	}

	private static int getMaxPage() {
		final List<Integer> pages = new ArrayList<>();
		for (final AdvancementTree tree : CustomAdvancements.getAdvancementManager().getAdvancementTrees()) {
			int page;
			try {
				page = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[0]);
			} catch (final NumberFormatException ex) {
				page = 1;
			}
			pages.add(page);
		}
		return Collections.max(pages);
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		switch (index) {
			case 18:
				if (page != 1)
					event.getWhoClicked().openInventory(new TreeGUI(page - 1).getInventory((Player) event.getWhoClicked()));
				break;
			case 26:
				if (page != getMaxPage())
					event.getWhoClicked().openInventory(new TreeGUI(page + 1).getInventory((Player) event.getWhoClicked()));
				break;
		}
	}
}
