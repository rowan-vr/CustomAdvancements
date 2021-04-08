package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AvailableAdvancementsGUI extends InventoryGUI{
	private final Map<Integer, List<CAdvancement>> items = new HashMap<>();
	private final Map<Integer, String> pageItems = new HashMap<>();
	private final int page;
	private final int maxPage;

	AvailableAdvancementsGUI(final int page, final Player player) {
		super(27, Lang.GUI_AVAILABLE_TITLE.getConfigValue(new String[]{String.valueOf(page), String.valueOf(getMaxPage(player))}, true));
		this.page = page;
		this.maxPage = getMaxPage(player);
		initPages(player);
	}

	@Override public Inventory getInventory(final Player player) {
		try {
			if (items.size() == 0) throw new NullPointerException();
			for (final CAdvancement advancement : items.get(page)) {
				final int index = inventory.firstEmpty();
				final ItemStack item;
				item = createGuiItem(advancement.getDisplayItem(), advancement.getDisplayName(), (advancement.getDescription() != null) ? advancement.getDescription() + "\\n" + Lang.GUI_ADVANCEMENTS_OPTIONS.getString() : Lang.GUI_ADVANCEMENTS_OPTIONS.getString());
				inventory.setItem(index, item);
				pageItems.put(index, advancement.getTree() + "." + advancement.getLabel());
			}
			setPaging(page, maxPage);
		} catch (final NullPointerException ignored) {
			inventory.setItem(13, createGuiItem(Material.BARRIER, Lang.GUI_AVAILABLE_NONE_NAME.getString(), Lang.GUI_AVAILABLE_NONE_LORE.getString()));
		}
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		final Player player = (Player) event.getWhoClicked();
		switch (index) {
			case 18:
				if (page != 1)
					player.openInventory(new AvailableAdvancementsGUI(page - 1, player).getInventory(player));
				break;
			case 26:
				if (page != maxPage)
					player.openInventory(new AvailableAdvancementsGUI(page + 1, player).getInventory(player));
				break;
			default:
				val clickedAdvancement = pageItems.get(index);
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
	}

	private static int getMaxPage(final Player player) {
		val max = (int) Math.ceil((double) CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId()).getActiveAdvancements().size() / (double) 18);
		return (max != 0) ? max : 1;
	}

	private void initPages(final Player player) {
		final List<CAdvancement> active = new LinkedList<>(CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId()).getAvailableAdvancements());
		for (final CAdvancement advancement : active) {
			boolean found = false;
			int i = 0;
			while (!found) {
				i++;
				if (Collections.frequency(items.keySet(), i) <= 18) {
					items.computeIfAbsent(i, k -> new LinkedList<>());
					items.get(i).add(advancement);
					found = true;
				}
			}
		}
	}
}
