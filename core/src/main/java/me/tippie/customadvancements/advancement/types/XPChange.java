package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.util.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XPChange extends AdvancementType<PlayerExpChangeEvent> {
    public XPChange() {
        super("xpchange", Lang.ADVANCEMENT_TYPE_XPCHANGE_UNIT.getString());
    }

    @EventHandler
    public void onXPGain(PlayerExpChangeEvent event) {
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(PlayerExpChangeEvent event, String value, String path) {
        if (value.equalsIgnoreCase("reach")) {
            progression(Math.round(event.getPlayer().getTotalExperience() + event.getAmount()),path, event.getPlayer().getUniqueId(),true);
        } else if (value.equalsIgnoreCase("gain") && event.getAmount() > 0) {
            progression(event.getAmount(), path, event.getPlayer().getUniqueId());
        } else if (value.equalsIgnoreCase("spend") && event.getAmount() < 0) {
            progression(Math.abs(event.getAmount()), path, event.getPlayer().getUniqueId());
        }
    }
}
