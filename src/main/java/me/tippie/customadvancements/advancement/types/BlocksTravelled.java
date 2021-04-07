package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlocksTravelled extends AdvancementType {

	public BlocksTravelled() {
		super("blockstravelled", Lang.ADVANCEMENT_TYPE_BLOCKSTRAVELLED_UNIT.getString());
	}

	@EventHandler
	public void onBlockMove(final PlayerMoveEvent e) {
		if (e.getTo() == null) return;
		if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
			progress(e, e.getPlayer().getUniqueId());
		}
	}

	@Override protected void onProgress(final Object e, final String value, final String path) {
		val event = (PlayerMoveEvent) e;
		if (event.getTo() == null) return;
		progression((int) event.getFrom().distance(event.getTo()), path, event.getPlayer().getUniqueId());
	}
}
