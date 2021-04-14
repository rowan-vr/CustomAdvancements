package me.tippie.customadvancements.commands;

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
            Utils.showToast((Player) sender, "test", "another test");
        }
    }
}
