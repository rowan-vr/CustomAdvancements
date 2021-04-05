package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.ArrayList;
import java.util.List;

public class Statistic extends AdvancementType {
	public Statistic() {
		super("statistic", Lang.ADVANCEMENT_TYPE_STATISTIC_UNIT.getString());
	}

	@EventHandler
	public void onStatisticChange(final PlayerStatisticIncrementEvent event) {
		progress(event, event.getPlayer().getUniqueId());
	}

	@Override protected void onProgress(final Object event, final String value, final String path) {
		final PlayerStatisticIncrementEvent playerStatisticIncrementEvent = (PlayerStatisticIncrementEvent) event;
		if (value == null || value.equalsIgnoreCase("any")) {
			progression(1, path, playerStatisticIncrementEvent.getPlayer().getUniqueId());
		} else {
			final List<org.bukkit.Statistic> statistics = new ArrayList<>();
			final String[] statisticStrings = value.split(",");
			for (final String materialString : statisticStrings)
				statistics.add(org.bukkit.Statistic.valueOf(materialString));
			if (statistics.contains(playerStatisticIncrementEvent.getStatistic())) {
				final int increment = playerStatisticIncrementEvent.getNewValue() - playerStatisticIncrementEvent.getPreviousValue();
				progression(increment, path, playerStatisticIncrementEvent.getPlayer().getUniqueId());
			}
		}
	}
}
