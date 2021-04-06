package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.Callable;

public class Chat extends AdvancementType {
	public Chat() {
		super("chat", Lang.ADVANCEMENT_TYPE_CHAT_UNIT.getString());
	}

	@EventHandler(ignoreCancelled = true)
	public void onAsyncChat(final AsyncPlayerChatEvent event) {
		Bukkit.getScheduler().callSyncMethod(CustomAdvancements.getInstance(), new Callable<Object>(){
			@Override public Object call() throws Exception {
				progress(event, event.getPlayer().getUniqueId());
				return true;
			}
		});
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
