package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftItem extends AdvancementType<CraftItemEvent> {
	public CraftItem() {
		super("craftitem", Lang.ADVANCEMENT_TYPE_CRAFTITEM_UNIT.getString());
	}

	@EventHandler
	public void onBlockPlace(final CraftItemEvent event) {
		progress(event, event.getView().getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final CraftItemEvent event, String value, final String path) {
		val player = event.getView().getPlayer();

		int recipeAmount = event.getRecipe().getResult().getAmount();

		switch (event.getClick()) {
			case NUMBER_KEY:
				if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null)
					recipeAmount = 0;
				break;
			case DROP:
			case CONTROL_DROP:
				ItemStack cursor = event.getCursor();
				if (cursor == null || cursor.getType() != Material.AIR)
					recipeAmount = 0;
				break;
			case SHIFT_RIGHT:
			case SHIFT_LEFT:
				if (recipeAmount == 0)
					break;

				int maxCraftable = getMaxCraftAmount(event.getInventory());
				int capacity = fits(event.getRecipe().getResult(), event.getView().getBottomInventory());
				if (capacity < maxCraftable)
					maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;

				recipeAmount = maxCraftable;
				break;
			default:
		}

		if (value == null || value.equalsIgnoreCase("any")) {
			progression(recipeAmount, path, player.getUniqueId());
		} else {
			boolean not = false;
			if (value.startsWith("!")) {
				value = value.substring(1);
				not = true;
			}
			final List<Material> materials = new ArrayList<>();
			final String[] materialStrings = value.split(",");
			for (final String materialString : materialStrings)
				materials.add(Material.getMaterial(materialString.toUpperCase()));
			if ((materials.contains(event.getRecipe().getResult().getType()) && !not) || (!materials.contains(event.getRecipe().getResult().getType()) && not)) {
				progression(recipeAmount, path, player.getUniqueId());
			}
		}
	}

	private static int getMaxCraftAmount(CraftingInventory inventory) {
		if (inventory.getResult() == null)
			return 0;

		int resultCount = inventory.getResult().getAmount();
		int materialCount = Integer.MAX_VALUE;

		for (ItemStack item : inventory.getMatrix())
			if (item != null && item.getAmount() < materialCount)
				materialCount = item.getAmount();

		return resultCount * materialCount;
	}

	private static int fits(ItemStack item, Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		int result = 0;

		for (ItemStack is : contents)
			if (is == null)
				result += item.getMaxStackSize();
			else if (is.isSimilar(item))
				result += Math.max(item.getMaxStackSize() - is.getAmount(), 0);

		return result;
	}
}
