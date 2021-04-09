package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RequirementsGUI extends InventoryGUI {

	private final CAdvancement advancement;
	private final Map<Integer, List<AdvancementRequirement>> items = new HashMap<>();
	private final String path;
	private final int page;
	private final int maxPage;

	RequirementsGUI(final String path, final int page, final Player player) throws InvalidAdvancementException {
		super(27, Lang.GUI_REQUIREMENTS_TITLE.getConfigValue(new String[]{CustomAdvancements.getAdvancementManager().getAdvancement(path).getLabel(), String.valueOf(page), String.valueOf(getMaxPage(path))}, true));
		this.advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
		this.page = page;
		this.path = path;
		this.maxPage = getMaxPage(path);
		initPages(player);
	}

	@Override public Inventory getInventory(final Player player, final boolean ignoreHistory) {
		val guiHistory = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId()).getGuiHistory();
		val string = "requirements:"+path+":"+page;
		if (!ignoreHistory)
			guiHistory.add(string);
		else
			replaceLast(guiHistory,string);

		try {
			if (items.size() == 0) throw new NullPointerException();
			for (final AdvancementRequirement requirement : items.get(page)) {
				final int index = inventory.firstEmpty();
				final ItemStack item;
				if (requirement.isMet(player)) {
					item = createGuiItem(requirement.getDisplayItem(), ChatColor.DARK_GREEN + requirement.getName(), ChatColor.GREEN + requirement.getMessage(player));
					final ItemMeta meta = item.getItemMeta();
					assert meta != null;
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				} else {
					item = createGuiItem(requirement.getDisplayItem(), ChatColor.DARK_RED + requirement.getName(), ChatColor.RED + requirement.getMessage(player));
				}
				inventory.setItem(index, item);
				setPaging(page, maxPage);
			}
		} catch (final NullPointerException ignored) {
			inventory.setItem(13, createGuiItem(Material.BARRIER, Lang.GUI_REQUIREMENTS_NO_REQUIREMENTS_NAME.getString(), Lang.GUI_REQUIREMENTS_NO_REQUIREMENTS_LORE.getString()));
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
						player.openInventory(new RequirementsGUI(path, page - 1, player).getInventory(player, true));
					break;
				case 26:
					if (page != maxPage)
						player.openInventory(new RequirementsGUI(path, page + 1, player).getInventory(player, true));
					break;
			}
		} catch (InvalidAdvancementException ignored){

		}
	}

	private static int getMaxPage(final String path) throws InvalidAdvancementException {
		val max = (int) Math.ceil((double) CustomAdvancements.getAdvancementManager().getAdvancement(path).getRequirements().size() / 18D);
		return (max != 0) ? max : 1;
	}

	private void initPages(final Player player) {
		final List<AdvancementRequirement> notCompleted = new LinkedList<>(advancement.getRequirements(false, player));
		final List<AdvancementRequirement> completed = new LinkedList<>(advancement.getRequirements(true, player));
		for (final AdvancementRequirement requirement : notCompleted) {
			boolean found = false;
			int i = 0;
			while (!found) {
				i++;
				if (Collections.frequency(items.keySet(), i) <= 18) {
					items.computeIfAbsent(i, k -> new LinkedList<>());
					items.get(i).add(requirement);
					found = true;
				}
			}
		}
		for (final AdvancementRequirement requirement : completed) {
			boolean found = false;
			int i = 0;
			while (!found) {
				i++;
				if (Collections.frequency(items.keySet(), i) <= 18) {
					items.computeIfAbsent(i, k -> new LinkedList<>());
					items.get(i).add(requirement);
					found = true;
				}
			}
		}
	}
}
