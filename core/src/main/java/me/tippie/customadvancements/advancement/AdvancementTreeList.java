package me.tippie.customadvancements.advancement;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.advancement.requirement.types.Advancement;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AdvancementTreeList {
	private final ImmutableSet<ChildAdvancement> nodes;

	public static AdvancementTreeList build(AdvancementTree tree) {
		Set<ChildAdvancement> parentList = makeParents(tree);
		makeChildren(tree, parentList);
		return new AdvancementTreeList(ImmutableSet.copyOf(parentList));
	}

	private static Set<ChildAdvancement> makeParents(AdvancementTree tree) {
		Set<ChildAdvancement> advancements = new HashSet<>();
		for (CAdvancement advancement : tree.getAdvancements()) {
			Optional<CAdvancement> parent = getParent(tree, advancement);
			if (parent.isPresent()) {
				advancements.add(new ChildAdvancement(advancement, parent.get()));
			} else {
				advancements.add(new ChildAdvancement(advancement, null));
			}
		}
		return advancements;
	}

	private static void makeChildren(AdvancementTree tree, Set<ChildAdvancement> advancements) {
		for (ChildAdvancement child : advancements) {
			if (child.parent != null) {
				advancements.stream()
						.filter(parent -> parent.advancement == child.parent)
						.forEach(parent -> parent.children.add(child));
			}
		}
	}

	private static Optional<CAdvancement> getParent(AdvancementTree tree, CAdvancement advancement) {
		return advancement.getRequirements().stream()
				.filter(req -> req.getType() instanceof Advancement)
				.map(AdvancementRequirement::getValue)
				.map(req -> {
					try {
						return tree.getAdvancement(req.split("\\.")[1]);
					} catch ( InvalidAdvancementException e) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.findFirst();
	}

	@AllArgsConstructor (access = AccessLevel.PRIVATE) @Getter
	public static class ChildAdvancement{
		private CAdvancement advancement;
		private @Nullable CAdvancement parent;
		private final Set<ChildAdvancement> children = new HashSet<>();
	}
}
