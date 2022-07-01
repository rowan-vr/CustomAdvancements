package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlocksTravelled extends AdvancementType<PlayerMoveEvent> {

	public BlocksTravelled() {
		super("blockstravelled", Lang.ADVANCEMENT_TYPE_BLOCKSTRAVELLED_UNIT.getString());
	}

	@EventHandler
	public void onBlockMove(final PlayerMoveEvent e) {
		if (e.getTo() == null) return;
		if ((int) e.getFrom().getX() != (int) e.getTo().getX() || (int) e.getFrom().getZ() != (int) e.getTo().getZ()) {
			progress(e, e.getPlayer().getUniqueId());
		}
	}

	@Override protected void onProgress(final PlayerMoveEvent e, final String value, final String path) {
		if (e.getTo() == null) return;
		progression((int) Math.max(1, Math.round(e.getFrom().distance(e.getTo()))), path, e.getPlayer().getUniqueId());
	}
}
