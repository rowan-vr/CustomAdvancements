package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class TreeAdvancementsGUI extends InventoryGUI {
	private final int page;
	private int maxPage;
	private final AdvancementTree tree;
	private final Map<Integer, String> items = new HashMap<>();
	private final Map<Integer, LinkedList<CAdvancement>> autoItems = new HashMap<>();

	TreeAdvancementsGUI(final String tree, final int page) throws InvalidAdvancementException {
		super(27, Lang.GUI_ADVANCEMENTS_TITLE.getConfigValue(new String[]{CustomAdvancements.getAdvancementManager().getAdvancementTree(tree).getOptions().getDisplayName(), String.valueOf(page), String.valueOf(getMaxPage(tree))}, true));
		this.page = page;
		this.tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(tree);
		initPages();
	}

	@Override public Inventory getInventory(final Player player, final boolean ignoreHistory) {
		val guiHistory = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId()).getGuiHistory();
		val string = "advancements:" + tree.getLabel() + ":" + page;
		if (!ignoreHistory)
			guiHistory.add(string);
		else
			replaceLast(guiHistory, string);

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
				val item = createGuiItem(advancement.getDisplayItem(), advancement.getDisplayName(), (advancement.getDescription() != null) ? advancement.getDescription(player) + "\\n" + Lang.GUI_ADVANCEMENTS_OPTIONS.getString() : Lang.GUI_ADVANCEMENTS_OPTIONS.getString());
				inventory.setItem(index, item);
				items.put(index, tree.getLabel() + "." + advancement.getLabel());
			}
		}
		try {
			for (final CAdvancement advancement : autoItems.get(page)) {
				final int index = inventory.firstEmpty();
				val item = createGuiItem(advancement.getDisplayItem(), advancement.getDisplayName(), (advancement.getDescription() != null) ? advancement.getDescription(player) + "\\n" + Lang.GUI_ADVANCEMENTS_OPTIONS.getString() : Lang.GUI_ADVANCEMENTS_OPTIONS.getString());
				inventory.setItem(index, item);
				items.put(index, tree.getLabel() + "." + advancement.getLabel());
			}
		} catch (final NullPointerException ignored) {

		}
		setPaging(page, maxPage);
		setBack(22);
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		final Player player = (Player) event.getWhoClicked();
		try {
			switch (index) {
				case 18:
					if (page != 1)
						player.openInventory(new TreeAdvancementsGUI(tree.getLabel(), page - 1).getInventory(player, true));
					break;
				case 26:
					if (page != maxPage)
						player.openInventory(new TreeAdvancementsGUI(tree.getLabel(), page + 1).getInventory(player, true));
					break;
				default:
					val clickedAdvancement = items.get(index);
					if (clickedAdvancement == null) {
						break;
					} else {
						try {
							player.openInventory(new AdvancementOptionsGUI(clickedAdvancement).getInventory(player));
						} catch (final InvalidAdvancementException ex) {
							event.getView().close();
							player.sendMessage(Lang.GUI_TREES_INVALID_TREE.getString(false));
						}
					}

			}
		} catch (final InvalidAdvancementException ex) {
			event.getView().close();
			player.sendMessage(Lang.GUI_TREES_INVALID_TREE.getString(false));
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
		for (final CAdvancement ignored : autoPlaced) {
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
