package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.raid.RaidFinishEvent;

public class RaidFinish extends AdvancementType{
    public RaidFinish() {
        super("raidfinish", Lang.ADVANCEMENT_TYPE_RAIDFINISH.getString());
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent event){
        for (Player player : event.getWinners()){
            progress(player, player.getUniqueId());
        }
    }

    @Override
    protected void onProgress(Object e, String value, String path) {
        val player = (Player) e;
        progression(1, path, player.getUniqueId());
    }
}
