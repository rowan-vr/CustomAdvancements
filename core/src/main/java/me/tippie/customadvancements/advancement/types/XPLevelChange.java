package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class XPLevelChange extends AdvancementType<PlayerLevelChangeEvent> {
    public XPLevelChange() {
        super("xplevelchange", Lang.ADVANCEMENT_TYPE_XPLEVELCHANGE_UNIT.getString());
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event){
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(PlayerLevelChangeEvent event, String value, String path) {
        int changeAmount = event.getNewLevel() - event.getOldLevel();
        if (value.equalsIgnoreCase("reach")) {
            progression(event.getPlayer().getLevel(),path, event.getPlayer().getUniqueId(),true);
        } else if (value.equalsIgnoreCase("gain") && changeAmount > 0 ) {
            progression(changeAmount, path, event.getPlayer().getUniqueId());
        } else if (value.equalsIgnoreCase("spend") && changeAmount < 0) {
            progression(Math.abs(changeAmount), path, event.getPlayer().getUniqueId());
        }
    }
}
