package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat extends AdvancementType {
	public Chat() {
		super("chat", Lang.ADVANCEMENT_TYPE_CHAT_UNIT.getString());
	}

	@EventHandler(ignoreCancelled = true)
	public void onAsyncChat(final AsyncPlayerChatEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, final String value, final String path) {
		final AsyncPlayerChatEvent asyncPlayerChatEvent = (AsyncPlayerChatEvent) event;
		if (value == null || value.equalsIgnoreCase("any") || value.equalsIgnoreCase("")) {
			progression(1, path, asyncPlayerChatEvent.getPlayer().getUniqueId());
		} else {
			val message = asyncPlayerChatEvent.getMessage();
			if (message.toLowerCase().contains(value.toLowerCase()))
				progression(1, path, asyncPlayerChatEvent.getPlayer().getUniqueId());
		}
	}
}
