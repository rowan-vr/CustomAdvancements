package me.tippie.customadvancements.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CAPlayerManager {
	private final Map<UUID, CAPlayer> caPlayers = new ConcurrentHashMap<>();

}
