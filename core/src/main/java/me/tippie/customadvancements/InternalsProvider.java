package me.tippie.customadvancements;

import com.google.common.collect.Lists;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface InternalsProvider<T, T1, T2> {

	CompletableFuture<Void> loadAdvancements(List<AdvancementTree> trees);

	CompletableFuture<Void> sendAdvancements(Player player, boolean clear);

	CompletableFuture<Void> updateAdvancement(Player player, CAdvancement... advancements);

	void registerAdvancementTabListener(Player player);

	String getResourceLocationOfAdvancement(T advancement);
	String getResourceLocationOfNms(T1 location);

	T1 getNmsLocationFromString(String location);
	default T1 getNmsLocationFromAdvancement(T advancement) {
		return getNmsLocationFromString(getResourceLocationOfAdvancement(advancement));
	}

	List<T> getTreeFriendlyListList(Collection<T> advancements);

	CompletableFuture<Void> sendAdvancementPacketImpl(Player player, boolean clear, Collection<T> advancements, Set<T1> remove, Map<T1, T2> progress);

	default CompletableFuture<Void> sendAdvancementPacket(Player player, boolean clear, Collection<T> advancements, Set<T1> remove, Map<T1, T2> progress) {
		return CompletableFuture.runAsync(() -> {
			Queue<Collection<T>> advancementQueue = new LinkedList<>(Lists.partition(getTreeFriendlyListList(advancements), CustomAdvancements.ADVANCEMENTS_PER_PACKET));
			Queue<List<Map.Entry<T1, T2>>> progressQueue = new LinkedList<>(Lists.partition(new ArrayList<>(progress.entrySet()), CustomAdvancements.PROGRESS_PER_PACKET));

			sendAdvancementPacketImpl(player, clear,
					new ArrayList<>(),
					remove,
					new HashMap<>()
			).join();

			while (advancementQueue.peek() != null) {
				sendAdvancementPacketImpl(player, false,
						advancementQueue.poll(),
						new HashSet<>(),
						new HashMap<>()
				).join();
			}

			while (progressQueue.peek() != null) {
				sendAdvancementPacketImpl(player, false,
						new ArrayList<>(),
						new HashSet<>(),
						progressQueue.poll().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
				).join();
			}
		});
	}
}
