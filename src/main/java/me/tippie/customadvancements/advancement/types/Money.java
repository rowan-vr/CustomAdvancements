package me.tippie.customadvancements.advancement.types;


import lombok.val;
import me.tippie.customadvancements.util.Lang;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.event.EventHandler;

public class Money extends AdvancementType {
    public Money() {
        super("money", Lang.ADVANCEMENT_TYPE_MONEY_UNIT.getString());
    }

    @EventHandler
    public void onBalanceUpdate(UserBalanceUpdateEvent event){
        progress(event,event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(Object e, String value, String path) {
        val event = (UserBalanceUpdateEvent) e;
        val player = event.getPlayer();
        val change = event.getOldBalance().intValue() - event.getNewBalance().intValue();
        if (value.equalsIgnoreCase("reach")) {
            progression(event.getNewBalance().intValue(),path,player.getUniqueId(),true);
        } else if (value.equalsIgnoreCase("gain") && change > 0) {
            progression(change, path, player.getUniqueId());
        } else if (value.equalsIgnoreCase("spend") && change < 0) {
            progression(Math.abs(change), path, player.getUniqueId());
        }
    }
}
