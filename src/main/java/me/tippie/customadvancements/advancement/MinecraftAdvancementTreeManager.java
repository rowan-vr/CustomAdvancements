package me.tippie.customadvancements.advancement;

import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.AdvancementFactory;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This manages the minecraft adancement trees
 * <b>This class requires protocollib.</b>
 */
public class MinecraftAdvancementTreeManager implements Listener {

    public static void clearAdvancements(Plugin plugin) throws IOException {
        File file = new File(Bukkit.getWorlds().get(0).getWorldFolder(), String.join(File.separator, "data", "advancements", new NamespacedKey(plugin,"na").getNamespace()));
        Files.walk(file.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        Bukkit.reloadData();
    }



    public static void addAdvancements(Plugin plugin, AdvancementTree tree){
        AdvancementFactory factory = new AdvancementFactory(plugin,false,false);

        Advancement root = factory.getRoot(tree.getLabel()+"/root",
                tree.getOptions().getDisplayName(),
                tree.getOptions().getDescription(),
                tree.getOptions().getDisplayItem().getType(),
                "block/dirt");
        root.activate(false);

        final HashMap<String, Advancement> advancements = new HashMap<>();

        for (CAdvancement advancement : tree.getAdvancements()) {
            factory.getCountedImpossible(tree.getLabel()+"/"+advancement.getLabel()
                    ,root,
                    advancement.getDisplayName()
                    ,advancement.getDescription() != null ? advancement.getDescription() : "No description set"
                    ,advancement.getDisplayItem().getType(),
                    Math.min(1,advancement.getMaxProgress())).setHidden(false);
        }
        for (CAdvancement advancement : tree.getAdvancements()) {
            Advancement advancement1 = advancements.get(advancement.getLabel());

            AdvancementRequirement requirement = advancement.getRequirements().stream()
                    .filter(req -> req.getType() instanceof me.tippie.customadvancements.advancement.requirement.types.Advancement)
                    .findFirst().orElse(null);
            if (requirement==null) continue;
            try {
                CAdvancement advancementRequirement = CustomAdvancements.getAdvancementManager().getAdvancement(requirement.getValue());
                if (advancementRequirement.getTree().equals(tree.getLabel()))
                    advancement1.addRequirement("");
            } catch (InvalidAdvancementException ignored){

            }
        }


        Bukkit.reloadData();
    }
}
