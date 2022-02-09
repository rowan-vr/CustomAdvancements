package me.tippie.customadvancements.advancement;

import com.google.common.base.Charsets;
import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.AdvancementFactory;
import hu.trigary.advancementcreator.trigger.ImpossibleTrigger;
import me.tippie.customadvancements.CustomAdvancements;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.player.datafile.AdvancementProgress;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * This manages the minecraft adancement trees
 */
public class MinecraftAdvancementTreeManager implements Listener {

	public static void clearAdvancements(Plugin plugin) throws IOException {
		File file = new File(Bukkit.getWorlds().get(0).getWorldFolder(), String.join(File.separator, "datapacks", "bukkit", "data", new NamespacedKey(plugin, "na").getNamespace(), "advancements"));
		Files.walk(file.toPath())
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
		Bukkit.reloadData();
	}


	public static void addAdvancements(Plugin plugin, AdvancementTree tree) {
		AdvancementFactory factory = new AdvancementFactory(plugin, false, false);

		Advancement root = factory.getRoot(tree.getLabel() + "/root",
				tree.getOptions().getDisplayName(),
				tree.getOptions().getDescription(),
				tree.getOptions().getDisplayItem().getType(),
				tree.getOptions().getMinecraftGuiBackground());
		root.activate(false);

		final HashMap<String, Advancement> advancements = new HashMap<>();

        for (CAdvancement advancement : tree.getAdvancements()) {
            int triggerCount;
            switch (advancement.getMinecraftProgressType()){
                case NONE: triggerCount = 1; break;
                case COUNT: triggerCount = Math.max(1,advancement.getMaxProgress());break;
                case PERCENTAGE: triggerCount = 100; break;
                default: triggerCount = 1;
            }

			Advancement newAdvancement = factory.getCountedImpossible(tree.getLabel() + "/" + advancement.getLabel()
							, root,
							advancement.getDisplayName()
							, advancement.getDescription() != null ? advancement.getDescription() : "No description set"
							, advancement.getDisplayItem().getType(),
							triggerCount
					)
					.setHidden(false)
					.setFrame(advancement.getMinecraftGuiFrame())
					.setToast(advancement.isMinecraftToast())
					.setAnnounce(advancement.isMinecraftChatAnnounce());
			for (int i = 0; i < triggerCount; ++i) {
				newAdvancement.addRequirement(String.valueOf(i));
			}
			newAdvancement.activate(false);
			advancements.put(advancement.getLabel(), newAdvancement);
		}
		for (CAdvancement advancement : tree.getAdvancements()) {
			Advancement advancement1 = advancements.get(advancement.getLabel());

			AdvancementRequirement requirement = advancement.getRequirements().stream()
					.filter(req -> req.getType() instanceof me.tippie.customadvancements.advancement.requirement.types.Advancement)
					.findFirst().orElse(null);
			if (requirement != null) {
				try {
					CAdvancement advancementRequirement = CustomAdvancements.getAdvancementManager().getAdvancement(requirement.getValue());
					advancement1.makeChild(advancements.get(advancementRequirement.getLabel()).getId());
					Method dataPackFolder = Bukkit.getUnsafe().getClass().getDeclaredMethod("getDataPackFolder");
					dataPackFolder.setAccessible(true);
					File bukkitDataPackFolder = (File) dataPackFolder.invoke(Bukkit.getUnsafe());

					NamespacedKey key = advancement1.getId();
					File file = new File(bukkitDataPackFolder, "data" + File.separator + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");

					com.google.common.io.Files.write(advancement1.toJson(), file, Charsets.UTF_8);

				} catch (InvalidAdvancementException ignored) {
					plugin.getLogger().log(Level.WARNING, "Failed to add requirement to advancement " + advancement.getLabel() + ": " + requirement.getValue());
				} catch (NoSuchMethodException ignored) {
					NamespacedKey key = advancement1.getId();
					File file = new File(Bukkit.getWorlds().get(0).getWorldFolder(), String.join(File.separator, "datapacks", "bukkit", "data" + File.separator + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json"));
					try {
						com.google.common.io.Files.write(advancement1.toJson(), file, Charsets.UTF_8);
					} catch (IOException e) {
						plugin.getLogger().log(Level.SEVERE, "Failed to write advancement to file", e);
					}
				} catch (InvocationTargetException | IllegalAccessException | IOException e) {
					plugin.getLogger().log(Level.SEVERE, "Failed to write advancement to file", e);
				}
			}
		}
		Bukkit.reloadData();
	}

	private static final int UPDATES_PER_TICK = 50;

	public static void updateProgress(Player player, CAdvancement advancement, AdvancementProgress progress, Plugin plugin) {

		org.bukkit.advancement.Advancement bukkitAdvancement = Bukkit.getAdvancement(new NamespacedKey(plugin, advancement.getTree() + "/" + advancement.getLabel()));
		org.bukkit.advancement.AdvancementProgress bukkitProgress = player.getAdvancementProgress(bukkitAdvancement);

		Bukkit.getScheduler().runTaskTimer(plugin, task -> {

			int operations = 0;

			if (bukkitProgress.getAwardedCriteria().size() > progress.getProgress()) {
				for (String criterion : bukkitProgress.getAwardedCriteria()) {
					if (Integer.parseInt(criterion) >= progress.getProgress()) {
						bukkitProgress.revokeCriteria(criterion);
						operations++;
						if (operations >= UPDATES_PER_TICK)
							return;
					}
				}
			} else if (bukkitProgress.getAwardedCriteria().size() < progress.getProgress()) {
				for (int i = bukkitProgress.getAwardedCriteria().size(); i < progress.getProgress(); i++) {
					bukkitProgress.awardCriteria(String.valueOf(i));
					operations++;
					if (operations >= UPDATES_PER_TICK)
						return;
				}
			} else {
				task.cancel();
			}

		},0,1);

	}
}
