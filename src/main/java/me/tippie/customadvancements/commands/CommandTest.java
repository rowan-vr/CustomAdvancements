package me.tippie.customadvancements.commands;

import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.MinecraftAdvancementTreeManager;
import me.tippie.customadvancements.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTest extends SubCommand{
    CommandTest() {
        super("test", "ca.test", "Used to test", "usage", new ArrayList<>());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                sender.sendMessage(args[1]);
                MinecraftAdvancementTreeManager.clearAdvancements(CustomAdvancements.getInstance());
                MinecraftAdvancementTreeManager.addAdvancements(CustomAdvancements.getInstance(), CustomAdvancements.getAdvancementManager().getAdvancementTree(args[1]));
            } catch (Exception e) {
                sender.sendMessage(e.getMessage());
            } catch (InvalidAdvancementException e) {
                e.printStackTrace();
            }
        }
    }
}
