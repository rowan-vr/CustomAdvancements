package me.tippie.customadvancements.advancement.types;

import lombok.val;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecuteCommand extends AdvancementType{



    public ExecuteCommand() {
        super("executecommand", Lang.ADVANCEMENT_TYPE_EXECUTECOMMAND_UNIT.getString());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        progress(event, event.getPlayer().getUniqueId());
    }

    @Override
    protected void onProgress(Object event, String value, String path) {
        val playerCommandPreprocessEvent = (PlayerCommandPreprocessEvent) event;
        val player = playerCommandPreprocessEvent.getPlayer();
        if (value == null || value.equalsIgnoreCase("any")) {
            progression(1, path, player.getUniqueId());
        } else {
            boolean not = false;
            if (value.startsWith("!")) {
                value = value.substring(1);
                not = true;
            }
            final String[] commandStrings = value.toLowerCase().split(",");
            final List<String> commands = new ArrayList<>(Arrays.asList(commandStrings));
            if ((commands.contains(playerCommandPreprocessEvent.getMessage().toLowerCase()) && !not) || (!commands.contains(playerCommandPreprocessEvent.getMessage().toLowerCase()) && not)) {
                progression(1, path, player.getUniqueId());
            }
        }
    }
}
