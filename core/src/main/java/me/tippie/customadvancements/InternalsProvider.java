package me.tippie.customadvancements;

import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface InternalsProvider {
	CompletableFuture<Void> loadAdvancements(List<AdvancementTree> trees);
	CompletableFuture<Void> sendAdvancements(Player player, boolean clear);
	CompletableFuture<Void> updateAdvancement(Player player, CAdvancement... advancements);
	void registerAdvancementTabListener(Player player);
}
