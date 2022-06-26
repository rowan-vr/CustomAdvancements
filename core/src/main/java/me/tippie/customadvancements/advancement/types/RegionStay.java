package me.tippie.customadvancements.advancement.types;


import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.tippie.customadvancements.CustomAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RegionStay extends AdvancementType {

	public RegionStay() {
		super("regionstay", "seconds");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomAdvancements.getInstance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				List<String> regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(player.getWorld()))
						.getApplicableRegionsIDs(BlockVector3.at(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ()));

				regions.forEach(region -> progress(new AbstractMap.SimpleEntry<>(region,player),player.getUniqueId()));
			}
		}, 20L, 20L);
	}

	@Override protected void onProgress(Object e, String value, String path) {
		Map.Entry<String, Player> entry = (Map.Entry<String, Player>) e;
		if (Arrays.stream(value.split(",")).anyMatch(str -> str.equals(entry.getKey()))){
			progression(1,path, entry.getValue().getUniqueId());
		}
	}
}
