package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class XPLevelChange extends AdvancementType{
    public XPLevelChange() {
        super("xplevelchange", Lang.ADVANCEMENT_TYPE_XPLEVELCHANGE_UNIT.getString());
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event){
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(Object event, String value, String path) {
        PlayerLevelChangeEvent playerLevelChangeEvent = (PlayerLevelChangeEvent) event;
        int changeAmount = playerLevelChangeEvent.getNewLevel() - playerLevelChangeEvent.getOldLevel();
        if (value.equalsIgnoreCase("reach")) {
            progression(playerLevelChangeEvent.getPlayer().getLevel(),path,playerLevelChangeEvent.getPlayer().getUniqueId(),true);
        } else if (value.equalsIgnoreCase("gain") && changeAmount > 0 ) {
            progression(changeAmount, path, playerLevelChangeEvent.getPlayer().getUniqueId());
        } else if (value.equalsIgnoreCase("spend") && changeAmount < 0) {
            progression(Math.abs(changeAmount), path, playerLevelChangeEvent.getPlayer().getUniqueId());
        }
    }
}
