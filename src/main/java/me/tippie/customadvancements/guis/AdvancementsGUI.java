package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class AdvancementsGUI extends InventoryGUI {
	private final int page;
	private int maxPage;
	private final AdvancementTree tree;
	private final Map<Integer, String> items = new HashMap<>();
	private final Map<Integer, LinkedList<CAdvancement>> autoItems = new HashMap<>();

	AdvancementsGUI(final String tree, final int page) throws InvalidAdvancementException {
		super(27, CustomAdvancements.getAdvancementManager().getAdvancementTree(tree).getOptions().getDisplayName() + " (" + page + "/" + getMaxPage(tree) + ")");
		this.page = page;
		this.tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(tree);
		initPages();
	}

	@Override public Inventory getInventory(final Player player) {
		for (final CAdvancement advancement : tree.getAdvancements()) {
			if (advancement.getGuiLocation().equalsIgnoreCase("auto")) continue;
			int itemPage, index;
			try {
				itemPage = Integer.parseInt(advancement.getGuiLocation().split(":")[0]);
				index = Integer.parseInt(advancement.getGuiLocation().split(":")[1]);
			} catch (final NumberFormatException ex) {
				itemPage = 0;
				index = 0;
			}
			if (itemPage == this.page) {
				val item = createGuiItem(Material.ACACIA_DOOR, advancement.getDisplayName(), (advancement.getDescription() != null) ? advancement.getDescription() + "\\n" + "Lang.GUI_TREES_ADVANCEMENTS.getString()" : "Lang.GUI_TREES_ADVANCEMENTS.getString()");
				inventory.setItem(index, item);
				items.put(index, advancement.getLabel());
			}
		}
		try {
			for (final CAdvancement advancement : autoItems.get(page)) {
				int index = inventory.firstEmpty();
				val item = createGuiItem(Material.ACACIA_DOOR, advancement.getDisplayName(), (advancement.getDescription() != null) ? advancement.getDescription() + "\\n" + "Lang.GUI_TREES_ADVANCEMENTS.getString()" : "Lang.GUI_TREES_ADVANCEMENTS.getString()");
				inventory.setItem(index, item);
				items.put(index, advancement.getLabel());
			}
		} catch (NullPointerException ignored){

		}
		if (maxPage != 1) {
			val backItem = !(this.page == 1) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_PREVIOUS_NAME.getString(), Lang.GUI_PAGE_PREVIOUS_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_FIRST_NAME.getString(), Lang.GUI_PAGE_FIRST_LORE.getString());
			val nextItem = !(this.page == maxPage) ? createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_PAGE_NEXT_NAME.getString(), Lang.GUI_PAGE_NEXT_LORE.getString()) : createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_PAGE_LAST_NAME.getString(), Lang.GUI_PAGE_LAST_LORE.getString());
			inventory.setItem(18, backItem);
			inventory.setItem(26, nextItem);
		}
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		final Player player = (Player) event.getWhoClicked();
		try {
			switch (index) {
				case 18:
					if (page != 1)
						player.openInventory(new AdvancementsGUI(tree.getLabel(), page - 1).getInventory(player));
					break;
				case 26:
					if (page != maxPage)
						player.openInventory(new AdvancementsGUI(tree.getLabel(), page + 1).getInventory(player));
					break;
				default:

			}
		} catch (final InvalidAdvancementException ex) {
			event.getView().close();
			player.sendMessage(Lang.GUI_TREES_INVALIDTREE.getString(false));
		}
	}


	private static int getMaxPage(final String tree) throws InvalidAdvancementException {
		final List<Integer> pages = new ArrayList<>();
		final Queue<CAdvancement> autoPlaced = new LinkedList<>();
		for (final CAdvancement advancement : CustomAdvancements.getAdvancementManager().getAdvancementTree(tree).getAdvancements()) {
			int page;
			if (advancement.getGuiLocation().equalsIgnoreCase("auto")) {
				autoPlaced.add(advancement);
			} else {
				try {
					page = Integer.parseInt(advancement.getGuiLocation().split(":")[0]);

				} catch (final NumberFormatException ex) {
					page = 1;
				}
				pages.add(page);
			}
		}
		for (final CAdvancement advancement : autoPlaced) {
			boolean found = false;
			int i = 0;
			while (!found) {
				i++;
				if (Collections.frequency(pages, i) <= 18) {
					pages.add(i);
					found = true;
				}
			}
		}
		return Collections.max(pages);
	}

	private void initPages() {
		final List<Integer> pages = new ArrayList<>();
		final Queue<CAdvancement> autoPlaced = new LinkedList<>();
		for (final CAdvancement advancement : tree.getAdvancements()) {
			int page;
			if (advancement.getGuiLocation().equalsIgnoreCase("auto")) {
				autoPlaced.add(advancement);
			} else {
				try {
					page = Integer.parseInt(advancement.getGuiLocation().split(":")[0]);

				} catch (final NumberFormatException ex) {
					page = 1;
				}
				pages.add(page);
			}
		}
		for (final CAdvancement advancement : autoPlaced) {
			boolean found = false;
			int i = 0;
			while (!found) {
				i++;
				if (Collections.frequency(pages, i) <= 18) {
					autoItems.computeIfAbsent(i, k -> new LinkedList<>());
					autoItems.get(i).add(advancement);
					pages.add(i);
					found = true;
				}
			}
		}
		this.maxPage = Collections.max(pages);
	}
}
