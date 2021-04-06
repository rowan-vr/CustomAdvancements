package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XPChange extends AdvancementType{
    public XPChange() {
        super("xpchange", Lang.ADVANCEMENT_TYPE_XPCHANGE_UNIT.getString());
    }

    @EventHandler
    public void onXPGain(PlayerExpChangeEvent event){
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(Object event, String value, String path) {
        PlayerExpChangeEvent playerExpChangeEvent = (PlayerExpChangeEvent) event;
        if (value.equalsIgnoreCase("gain") && playerExpChangeEvent.getAmount() > 0 ) {
            progression(playerExpChangeEvent.getAmount(), path, playerExpChangeEvent.getPlayer().getUniqueId());
        } else if (value.equalsIgnoreCase("spend") && playerExpChangeEvent.getAmount() < 0) {
            progression(Math.abs(playerExpChangeEvent.getAmount()), path, playerExpChangeEvent.getPlayer().getUniqueId());
        }
    }
}
