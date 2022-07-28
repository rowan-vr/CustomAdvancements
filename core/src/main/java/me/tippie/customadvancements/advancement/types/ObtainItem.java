package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ObtainItem extends AdvancementType<PlayerInventory> {
	public ObtainItem() {
		super("obtainitem", "items");
	}

	@EventHandler
	private void onInventoryEvent(InventoryClickEvent event){
		Bukkit.getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> progress(event.getWhoClicked().getInventory(), event.getWhoClicked().getUniqueId()), 5L);
	}

	@EventHandler
	private void onInventoryEvent(InventoryOpenEvent event){
		if (event.getPlayer() instanceof Player){
			progress(event.getPlayer().getInventory(), event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	private void onInventoryEvent(EntityDropItemEvent event){
		if (event.getEntity() instanceof Player){
			Bukkit.getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> progress(((Player) event.getEntity()).getInventory(), event.getEntity().getUniqueId()), 5L);
		}
	}

	@EventHandler
	private void onInventoryEvent(EntityPickupItemEvent event){
		if (event.getEntity() instanceof Player){
			Bukkit.getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> progress(((Player) event.getEntity()).getInventory(), event.getEntity().getUniqueId()), 5L);
		}
	}

	@Override protected void onProgress(PlayerInventory e, String value, String path) {
		Player player = (Player) e.getHolder();
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(countContent(e.getContents(), false), path, player.getUniqueId(),true);
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
			progression(countContent(e.getContents(),not,materials.toArray(new Material[]{})), path, player.getUniqueId(), true);

		}
	}

	private static int countContent(ItemStack[] content, boolean not, Material... materials) {
		if (materials.length == 0) {
			return Stream.of(content)
					.filter(Objects::nonNull)
					.mapToInt(ItemStack::getAmount)
					.sum();
		} else if (!not){
			return Stream.of(content)
					.filter(Objects::nonNull)
					.filter(item -> Stream.of(materials).anyMatch(material -> material == item.getType()))
					.mapToInt(ItemStack::getAmount)
					.sum();
		} else {
			return Stream.of(content)
					.filter(Objects::nonNull)
					.filter(item -> Stream.of(materials).noneMatch(material -> material == item.getType()))
					.mapToInt(ItemStack::getAmount)
					.sum();
		}
	}
}
