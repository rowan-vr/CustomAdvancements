package me.tippie.customadvancements.guis;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class AdvancementsGUI extends InventoryGUI {
	private final int page;
	private final AdvancementTree tree;

	AdvancementsGUI(final String tree, final int page) throws InvalidAdvancementException {
		super(27, CustomAdvancements.getAdvancementManager().getAdvancementTree(tree).getOptions().getDisplayName() + " (" + page + "/" + getMaxPage(tree) + ")");
		this.page = page;
		this.tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(tree);

	}

	@Override public Inventory getInventory(final Player player) {
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {

	}

	private int getMaxPage() throws InvalidAdvancementException {
		return getMaxPage(this.tree.getLabel());
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
}
