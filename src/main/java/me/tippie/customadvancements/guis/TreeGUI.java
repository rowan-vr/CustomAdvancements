package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class TreeGUI extends InventoryGUI {
	private final int page;
	private final Map<Integer, String> items = new HashMap<>();

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
				val item = createGuiItem(Material.OAK_SAPLING, tree.getOptions().getDisplayName(), (tree.getOptions().getDescription() != null) ? tree.getOptions().getDescription() + "\\n" + Lang.GUI_TREES_ADVANCEMENTS.getString() : Lang.GUI_TREES_ADVANCEMENTS.getString());
				inventory.setItem(index, item);
				items.put(index, tree.getLabel());
			}
			if (getMaxPage() != 1) {
				val backItem = !(this.page == 1) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_PREVIOUS_NAME.getString(), Lang.GUI_PAGE_PREVIOUS_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_FIRST_NAME.getString(), Lang.GUI_PAGE_FIRST_LORE.getString());
				val nextItem = !(this.page == getMaxPage()) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_NEXT_NAME.getString(), Lang.GUI_PAGE_NEXT_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_LAST_NAME.getString(), Lang.GUI_PAGE_LAST_LORE.getString());
				inventory.setItem(18, backItem);
				inventory.setItem(26, nextItem);
			}
		}
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		Player player = (Player) event.getWhoClicked();
		switch (index) {
			case 18:
				if (page != 1)
					player.openInventory(new TreeGUI(page - 1).getInventory(player));
				break;
			case 26:
				if (page != getMaxPage())
					player.openInventory(new TreeGUI(page + 1).getInventory(player));
				break;
			default:
				val clickedTree = items.get(index);
				if (clickedTree == null) {
					break;
				} else {
					try {
						System.out.println(clickedTree);
						System.out.println(index);
						System.out.println(player);
						player.openInventory(new AdvancementsGUI(clickedTree, 1).getInventory(player));
					} catch (final InvalidAdvancementException ex) {
						event.getView().close();
						player.sendMessage(Lang.GUI_TREES_INVALIDTREE.getString(false));
					}
				}
		}
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
}
