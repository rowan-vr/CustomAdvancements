package me.tippie.customadvancements.advancement;

import lombok.Getter;
import lombok.val;
import me.clip.placeholderapi.PlaceholderAPI;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.advancement.reward.AdvancementReward;
import me.tippie.customadvancements.advancement.types.AdvancementType;
import me.tippie.customadvancements.player.CAPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents an advancement in this plugin
 */
public class CAdvancement {
	/**
	 * The {@link AdvancementType} of this advancement.
	 */
	@Getter private final AdvancementType type;

	/**
	 * The progress required to complete this advancement
	 */
	@Getter private final int maxProgress;

	/**
	 * The value of the type, e.g. what block needs to be broken for {@link me.tippie.customadvancements.advancement.types.BlockBreak}
	 */
	@Getter private final String value;

	/**
	 * The label of this advancement
	 */
	@Getter private final String label;

	/**
	 * The tree this advancement belongs to
	 */
	@Getter private final String tree;
	/**
	 * The rewards when completing this advancement
	 */
	@Getter private final List<AdvancementReward> rewards;

	/**
	 * The requirements for this advancement to activate this advancement
	 */
	private final List<AdvancementRequirement> requirements;

	/**
	 * The name of this advancement in the GUI
	 */
	@Getter private final String displayName;

	/**
	 * The description of this advancement
	 */
	private final String description;

	/**
	 * The item this advancement is displayed as
	 */
	@Getter private final ItemStack displayItem;

	/**
	 * The location of this advancement in the GUI, can be 'auto'
	 */
	@Getter private final String guiLocation;

	/**
	 * The unit of this advancement
	 */
	@Getter private final String unit;

	/**
	 *
	 */
	@Getter private Frame minecraftGuiFrame;

	@Getter private final boolean minecraftToast;

	@Getter private final boolean minecraftChatAnnounce;

	private final MinecraftProgressType minecraftProgressType;

	/**
	 * Creates a new {@link CAdvancement}
	 *
	 * @param type              The label of the type of this advancement
	 * @param value             The value of this type
	 * @param maxProgress       The progress required to completed this advancement
	 * @param label             The label of this advancement
	 * @param tree              The label of the tree this advancement belongs to
	 * @param rewards           List of {@link AdvancementReward}'s of this advancement
	 * @param requirements      List of {@link AdvancementRequirement}'s of this advancement
	 * @param displayName       String of the display name of this advancement
	 * @param description       String of the description of this advancement
	 * @param displayItem       {@link ItemStack} for the display item for this advancement in GUI's
	 * @param guiLocation       String for the location of this advancement in the GUI formatted as 'page:index', can be 'auto'
	 * @param unit              The unit of this advancement for example: 'sand blocks broken' or 'times joined'
	 * @param minecraftGuiFrame
	 * @param minecraftToast
	 * @param minecraftChatAnnounce
	 * @see AdvancementType
	 */
	CAdvancement(final String type, final String value, final int maxProgress, final String label, final String tree, final List<AdvancementReward> rewards, final List<AdvancementRequirement> requirements, final String displayName, final String description, final ItemStack displayItem, final String guiLocation, final String unit, String minecraftGuiFrame, boolean minecraftToast, boolean minecraftChatAnnounce, String minecraftProgressType) {
		this.type = CustomAdvancements.getAdvancementManager().getAdvancementType(type);
		this.value = value;
		this.maxProgress = maxProgress;
		this.label = label;
		this.rewards = rewards;
		this.tree = tree;
		this.requirements = requirements;
		this.displayName = displayName;
		this.description = description;
		this.displayItem = displayItem;
		this.guiLocation = guiLocation;
		this.unit = (unit != null) ? unit : this.type.getDefaultUnit();
		this.minecraftToast = minecraftToast;
		this.minecraftChatAnnounce = minecraftChatAnnounce;
		if (minecraftGuiFrame != null)
			try {
				this.minecraftGuiFrame = Frame.valueOf(minecraftGuiFrame.toUpperCase());
			} catch (Exception e) {
				this.minecraftGuiFrame = Frame.TASK;
			}
		else
			this.minecraftGuiFrame = Frame.TASK;

		this.minecraftProgressType = MinecraftProgressType.valueOf(minecraftProgressType);
	}

	/**
	 * Executes the completed actions for this advancement
	 *
	 * @param uuid UUID of player who completed the advancement
	 */
	public void complete(final UUID uuid) {
		val player = Bukkit.getPlayer(uuid);
		assert player != null;

		if (this.minecraftChatAnnounce){
			BaseComponent advancement = new TextComponent("["+ChatColor.translateAlternateColorCodes('&',this.getDisplayName())+"]");
			if (this.getMinecraftGuiFrame() == Frame.CHALLENGE) advancement.setColor(ChatColor.DARK_PURPLE.asBungee());
			else advancement.setColor(ChatColor.GREEN.asBungee());

			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				BaseComponent description = new TextComponent(ChatColor.translateAlternateColorCodes('&',this.getDescription(onlinePlayer)));
				description.setColor(ChatColor.GRAY.asBungee());
				BaseComponent[] hover = new BaseComponent[]{description};
				advancement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
				BaseComponent component = new TranslatableComponent("chat.type.advancement." + this.getMinecraftGuiFrame().getValue().toLowerCase(), player.getName(), advancement);

				onlinePlayer.spigot().sendMessage(ChatMessageType.SYSTEM, component);
			}
		}
		for (final AdvancementReward reward : rewards) {
			reward.onComplete(player);
		}
	}

	/**
	 * Checks if all requirements of an advancement are met for a player
	 *
	 * @param player {@link Player} which this should be checked on
	 * @return true or false if this player meets the requirements
	 */
	public boolean meetRequirements(final Player player) {
		for (final AdvancementRequirement requirement : requirements) {
			if (!requirement.isMet(player)) return false;
		}
		return true;
	}

	/**
	 * Checks if all requirements of an advancement are met for a player
	 *
	 * @param player {@link Player} which this should be activated for
	 * @return true or false if this advancement is successfully activated
	 */
	public boolean activate(final Player player) {
		for (final AdvancementRequirement requirement : requirements) {
			if (!requirement.activate(player)) return false;
		}
		return true;
	}

	/**
	 * Gets the path of this advancement
	 *
	 * @return the path of an advancement formatted as 'treeLabel.advancementLabel'
	 */
	public String getPath() {
		return this.tree + "." + this.label;
	}

	/**
	 * The list of all requirements of this quest
	 *
	 * @return list with all {@link AdvancementRequirement}'s for this quest
	 */
	public List<AdvancementRequirement> getRequirements() {
		return requirements;
	}

	/**
	 * Gets the list of all requirements met or not met for a specific player
	 *
	 * @param isMet  return the list of met or not met requirements
	 * @param player the player that should be checked on if the requirements are met
	 * @return list with all met or not met {@link AdvancementRequirement}'s for a specific player
	 */
	public List<AdvancementRequirement> getRequirements(final boolean isMet, final Player player) {
		return requirements.stream().filter(requirement -> isMet == requirement.isMet(player)).collect(Collectors.toList());
	}

	/**
	 * Check whether this advancement is displayed in the minecraft advancement GUI
	 *
	 * @return true or false if this advancement is displayed in the minecraft advancement GUI
	 */
	public boolean isHidden() {
		try {
			return !CustomAdvancements.getAdvancementManager().getAdvancementTree(this.tree).getOptions().isMinecraftGuiDisplay();
		} catch (IllegalArgumentException | InvalidAdvancementException e) {
			return true;
		}
	}

	public String getDescription() {
		return description;
	}

	public String getDescription(Player player) {
		if (description == null) return "No Description";
		if (CustomAdvancements.getInstance().isPapiSupport() && player != null)
			return ChatColor.translateAlternateColorCodes('&',PlaceholderAPI.setPlaceholders(player,description));
		else
			return ChatColor.translateAlternateColorCodes('&',description);
	}

	public MinecraftProgressType getMinecraftProgressType() {
		if (minecraftProgressType == MinecraftProgressType.AUTO) {
			return this.maxProgress > 10000 ? MinecraftProgressType.PERCENTAGE : MinecraftProgressType.COUNT;
		} else {
			return minecraftProgressType;
		}
	}

	public boolean isAnnounced(Player player) {
		CAPlayer caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());
		return caPlayer.getAdvancementProgress().get(this.getPath()).isAnnounced();
	}

	public enum Frame {
		TASK("TASK"),
		GOAL("GOAL"),
		CHALLENGE("CHALLENGE");

		private final String value;

		Frame(String value){
			this.value = value;
		}

		public String getValue(){
			return value;
		}
	}

	public enum MinecraftProgressType {
		AUTO,
		COUNT,
		PERCENTAGE,
		NONE
	}
}
