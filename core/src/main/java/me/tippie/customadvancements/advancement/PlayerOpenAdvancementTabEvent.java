package me.tippie.customadvancements.advancement;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerOpenAdvancementTabEvent extends Event {
	@Getter private static final HandlerList handlerList = new HandlerList();
	private final Action action;
	private final NamespacedKey tabId;
	private final Player player;

	public PlayerOpenAdvancementTabEvent(@NotNull Player who, Action action, NamespacedKey tabId) {
		super(true);
		this.action = action;
		this.tabId = tabId;
		this.player = who;
	}


	@NotNull @Override public HandlerList getHandlers() {
		return PlayerOpenAdvancementTabEvent.handlerList;
	}

	public enum Action {
		OPEN,
		CLOSE
	}
}
