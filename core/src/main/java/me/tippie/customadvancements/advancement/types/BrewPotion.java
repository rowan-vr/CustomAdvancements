package me.tippie.customadvancements.advancement.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

public class BrewPotion extends AdvancementType<Map.Entry<UUID, ItemStack>> {

	public BrewPotion() {
		super("brew", "potions");
	}

	private final HashMap<Block, HashMap<Integer, UUID>> itemTrackMap = new HashMap<>();

	@EventHandler
	public void onBrewBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().equals(Material.BREWING_STAND))
			itemTrackMap.keySet().removeIf(k -> k.getX() == event.getBlock().getX() && k.getY() == event.getBlock().getY() && k.getZ() == event.getBlock().getZ());

	}

	@EventHandler
	public void onInventoryPut(InventoryClickEvent event) {
		if (!(event.getClickedInventory() instanceof BrewerInventory) || event.getRawSlot() > 2) return;
		itemTrackMap.computeIfAbsent(((BrewerInventory) event.getClickedInventory()).getHolder().getBlock(), k -> new HashMap<>());
		HashMap<Integer, UUID> trackMap = itemTrackMap.get(((BrewerInventory) event.getClickedInventory()).getHolder().getBlock());

		if ((event.isLeftClick() || event.isRightClick()) && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
			trackMap.put(event.getRawSlot(), null);
		}
		if ((event.isLeftClick() || event.isRightClick()) && event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR))
			trackMap.put(event.getRawSlot(), event.getWhoClicked().getUniqueId());

	}

	@EventHandler
	public void onBrew(BrewEvent e) throws NoSuchFieldException, IllegalAccessException {
		Field field = e.getClass().getDeclaredField("results");
		field.setAccessible(true);
		ArrayList<ItemStack> results = (ArrayList<ItemStack>) field.get(e);
		for (int i = 0; i < 3; i++) {
			if (itemTrackMap.get(e.getContents().getHolder().getBlock()) == null) continue;
			UUID uuid = itemTrackMap.get(e.getContents().getHolder().getBlock()).get(i);
			if (uuid != null) {
				progress(new AbstractMap.SimpleEntry<>(uuid, results.get(i)), uuid);
			}
		}
	}

	@Override
	protected void onProgress(Map.Entry<UUID, ItemStack> event, String value, String path) {
		ItemStack potion = event.getValue();
		UUID uuid = event.getKey();
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		PotionEffectType effect = meta.getBasePotionData().getType().getEffectType();
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, uuid);
		} else {
			boolean not = false;
			if (value.startsWith("!")) {
				value = value.substring(1);
				not = true;
			}
			final List<PotionEffectType> types = new ArrayList<>();
			final String[] typeStrings = value.split(",");
			for (final String typeString : typeStrings)
				types.add(PotionEffectType.getByName(typeString));


			if ((types.contains(effect) && !not) || (!types.contains(effect) && not)) {
				progression(1, path, uuid);
			}

		}
	}
}
