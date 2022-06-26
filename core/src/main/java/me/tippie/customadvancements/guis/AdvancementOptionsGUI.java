package me.tippie.customadvancements.guis;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.player.CAPlayer;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class AdvancementOptionsGUI extends InventoryGUI {
	private final String path;
	private final CAdvancement advancement;
	private final AdvancementTree tree;
	private CAPlayer caPlayer;

	AdvancementOptionsGUI(final String path) throws InvalidAdvancementException {
		super(45, Lang.GUI_ADVANCEMENT_OPTIONS_TITLE.getConfigValue(new String[]{CustomAdvancements.getAdvancementManager().getAdvancement(path).getDisplayName()}, true));
		this.path = path;
		this.advancement = CustomAdvancements.getAdvancementManager().getAdvancement(path);
		this.tree = CustomAdvancements.getAdvancementManager().getAdvancementTree(path.split("\\.")[0]);
	}

	@Override public Inventory getInventory(final Player player, final boolean ignoreHistory) throws InvalidAdvancementException {
		val guiHistory = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId()).getGuiHistory();
		val string = "advancementoptions:" + path;
		if (!ignoreHistory)
			guiHistory.add(string);
		else
			replaceLast(guiHistory,string);

		caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());
		int next = 0;
		final ItemStack backgroundItem = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
		while (next != -1) {
			inventory.setItem(next, backgroundItem);
			next = inventory.firstEmpty();
		}

		final ItemStack activateItem = (caPlayer.checkIfAdvancementActive(path) || caPlayer.checkIfAdvancementCompleted(path)) ? createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_LORE.getString()) : createGuiItem(Material.LIME_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATE_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATE_LORE.getString());
		inventory.setItem(10, activateItem);

		final ItemStack requirementsItem = createGuiItem(Material.BOOK, Lang.GUI_ADVANCEMENT_OPTIONS_REQUIREMENTS_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_REQUIREMENTS_LORE.getConfigValue(new String[]{String.valueOf(advancement.getRequirements(true, player).size()), String.valueOf(advancement.getRequirements().size())}, true));
		inventory.setItem(11, requirementsItem);

		final ItemStack completedItem = !(caPlayer.checkIfAdvancementCompleted(path)) ? createGuiItem(Material.RED_WOOL, Lang.GUI_ADVANCEMENT_OPTIONS_NOT_COMPLETED_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_NOT_COMPLETED_LORE.getString()) : createGuiItem(Material.GREEN_WOOL, Lang.GUI_ADVANCEMENT_OPTIONS_COMPLETED_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_COMPLETED_LORE.getString());
		inventory.setItem(28, completedItem);

		val progress = caPlayer.getAdvancementProgress().get(path).getProgress();
		final ItemStack progressItem = createGuiItem(Material.ENCHANTED_BOOK, Lang.GUI_ADVANCEMENT_OPTIONS_PROGRESS_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_PROGRESS_LORE.getConfigValue(new String[]{String.valueOf(progress), String.valueOf(advancement.getMaxProgress()), advancement.getUnit(), String.valueOf(Math.round(((float) progress) / ((float) advancement.getMaxProgress()) * 100))}, true));
		inventory.setItem(29, progressItem);

		final ItemStack advancementItem = createGuiItem(advancement.getDisplayItem(), advancement.getDisplayName(), advancement.getDescription(player));
		inventory.setItem(23, advancementItem);

		final ItemStack treeItem = createGuiItem(tree.getOptions().getDisplayItem(), tree.getOptions().getDisplayName(), tree.getOptions().getDescription());
		inventory.setItem(25, treeItem);
		setBack(40);
		return inventory;
	}

	@Override public void onClick(final InventoryClickEvent event) {
		final int index = event.getRawSlot();
		final Player player = (Player) event.getWhoClicked();
		try {
			switch (index) {
				case 10:
					if (caPlayer.checkIfAdvancementActive(path) || caPlayer.checkIfAdvancementCompleted(path)) break;
					val result = caPlayer.activateAdvancement(path);
					if (result == null) {
						final ItemStack activatedItem = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_SUCCESSFULLY_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_SUCCESSFULLY_LORE.getString());
						inventory.setItem(10, activatedItem);
						CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> {
							try {
								final ItemStack activateItem = (caPlayer.checkIfAdvancementActive(path) || caPlayer.checkIfAdvancementCompleted(path)) ? createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_LORE.getString()) : createGuiItem(Material.LIME_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATE_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATE_LORE.getString());
								inventory.setItem(10, activateItem);
							} catch (final InvalidAdvancementException ignored) {
							}
						}, 100L);
						break;
					} else {
						final ItemStack notActivatedItem = createGuiItem(Material.RED_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_NOT_ACTIVATED_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_NOT_ACTIVATED_LORE.getConfigValue(new String[]{String.valueOf(result.size()), String.valueOf(advancement.getRequirements().size())}, true));
						inventory.setItem(10, notActivatedItem);
						CustomAdvancements.getInstance().getServer().getScheduler().runTaskLater(CustomAdvancements.getInstance(), () -> {
							try {
								final ItemStack activateItem = (caPlayer.checkIfAdvancementActive(path) || caPlayer.checkIfAdvancementCompleted(path)) ? createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATED_LORE.getString()) : createGuiItem(Material.LIME_STAINED_GLASS_PANE, Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATE_NAME.getString(), Lang.GUI_ADVANCEMENT_OPTIONS_ACTIVATE_LORE.getString());
								inventory.setItem(10, activateItem);
							} catch (final InvalidAdvancementException ignored) {
							}
						}, 100L);
						break;
					}
				case 11:
					try {
						player.openInventory(new RequirementsGUI(path, 1, player).getInventory(player));
					} catch (final InvalidAdvancementException ex) {
						event.getView().close();
						player.sendMessage(Lang.GUI_TREES_INVALID_TREE.getString(false));
					}
			}
		} catch (final InvalidAdvancementException ex) {
			CustomAdvancements.getInstance().getLogger().log(Level.WARNING, "The Advancements Options GUI tried to access an invalid advancement path for player " + player.getName() + " and advancement " + path);
		}
	}
}
