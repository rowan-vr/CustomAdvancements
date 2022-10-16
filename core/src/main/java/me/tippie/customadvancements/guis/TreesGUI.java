package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class TreesGUI extends InventoryGUI {
	private final int page;
	private final Map<Integer, String> items = new HashMap<>();
	private int maxPage;
	private final Map<Integer, List<AdvancementTree>> autoItems = new HashMap<>();

	public TreesGUI(final int page) {
		super(27, Lang.GUI_TREES_TITLE.getConfigValue(new String[]{String.valueOf(page), String.valueOf(getMaxPage())}, true));
		this.page = page;
		initPages();
	}

	@Override public Inventory getInventory(final Player player, final boolean ignoreHistory) {
		val guiHistory = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId()).getGuiHistory();
		val string = "tree:" + page;
		if (!ignoreHistory)
			guiHistory.add(string);
		else
			replaceLast(guiHistory,string);

		for (final AdvancementTree tree : CustomAdvancements.getAdvancementManager().getAdvancementTrees()) {
			if (tree.getOptions().getGuiLocation().equalsIgnoreCase("auto")) continue;
			int page, index;
			try {
				page = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[0]);
				index = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[1]);
			} catch (final NumberFormatException ex) {
				page = 1;
				index = 0;
			}
			if (page == this.page) {
				val item = createGuiItem(tree.getOptions().getDisplayItem(), tree.getOptions().getDisplayName(), (tree.getOptions().getDescription() != null) ? tree.getOptions().getDescription() + "\\n" + Lang.GUI_TREES_ADVANCEMENTS.getString() : Lang.GUI_TREES_ADVANCEMENTS.getString());
				inventory.setItem(index, item);
				items.put(index, tree.getLabel());
			}
		}
		try {
			for (final AdvancementTree tree : autoItems.get(page)) {
				final int index = inventory.firstEmpty();
				val item = createGuiItem(tree.getOptions().getDisplayItem(), tree.getOptions().getDisplayName(), (tree.getOptions().getDescription() != null) ? tree.getOptions().getDescription() + "\\n" + Lang.GUI_ADVANCEMENTS_OPTIONS.getString() : Lang.GUI_ADVANCEMENTS_OPTIONS.getString());
				inventory.setItem(index, item);
				items.put(index, tree.getLabel());
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
		switch (index) {
			case 18:
				if (page != 1)
					player.openInventory(new TreesGUI(page - 1).getInventory(player, true));
				break;
			case 26:
				if (page != maxPage)
					player.openInventory(new TreesGUI(page + 1).getInventory(player, true));
				break;
			default:
				val clickedTree = items.get(index);
				if (clickedTree == null) {
					break;
				} else {
					try {
						player.openInventory(new TreeAdvancementsGUI(clickedTree, 1).getInventory(player));
					} catch (final InvalidAdvancementException ex) {
						event.getView().close();
						player.sendMessage(Lang.GUI_TREES_INVALID_TREE.getString(false));
					}
				}
		}
	}

	private static int getMaxPage() {
		final List<Integer> pages = new ArrayList<>();
		final Queue<AdvancementTree> autoPlaced = new LinkedList<>();
		for (final AdvancementTree tree : CustomAdvancements.getAdvancementManager().getAdvancementTrees()) {
			int page;
			if (tree.getOptions().getGuiLocation().equalsIgnoreCase("auto")) {
				autoPlaced.add(tree);
			} else {
				try {
					page = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[0]);

				} catch (final NumberFormatException ex) {
					page = 1;
				}
				pages.add(page);
			}
		}
		for (final Object ignored : autoPlaced) {
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
		final Queue<AdvancementTree> autoPlaced = new LinkedList<>();
		for (final AdvancementTree tree : CustomAdvancements.getAdvancementManager().getAdvancementTrees()) {
			int page;
			if (tree.getOptions().getGuiLocation().equalsIgnoreCase("auto")) {
				autoPlaced.add(tree);
			} else {
				try {
					page = Integer.parseInt(tree.getOptions().getGuiLocation().split(":")[0]);

				} catch (final NumberFormatException ex) {
					page = 1;
				}
				pages.add(page);
			}
		}
		for (final AdvancementTree tree : autoPlaced) {
			boolean found = false;
			int i = 0;
			while (!found) {
				i++;
				if (Collections.frequency(pages, i) <= 18) {
					autoItems.computeIfAbsent(i, k -> new LinkedList<>());
					autoItems.get(i).add(tree);
					pages.add(i);
					found = true;
				}
			}
		}
		this.maxPage = Collections.max(pages);
	}
}
