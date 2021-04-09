package me.tippie.customadvancements;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class v1_16_R1 implements InternalsProvider {
	@Override public TextComponent setHoverText(final TextComponent message, final String hovermessage) {
		final HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hovermessage));
		message.setHoverEvent(event);
		return message;
	}
}
