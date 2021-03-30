package me.tippie.customadvancements;

import lombok.Getter;
import me.tippie.customadvancements.advancement.AdvancementManager;
import me.tippie.customadvancements.advancement.types.BlockBreak;
import me.tippie.customadvancements.commands.CommandListener;
import me.tippie.customadvancements.listeners.PlayerJoinListener;
import me.tippie.customadvancements.player.datafile.AdvancementProgressFile;
import me.tippie.customadvancements.utils.ConfigWrapper;
import me.tippie.customadvancements.utils.Lang;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomAdvancements extends JavaPlugin {
	private final ConfigWrapper messagesFile = new ConfigWrapper(this, "", "messages.yml");
	@Getter
	private static CustomAdvancements instance;
	@Getter
	private static CommandListener commandListener;
	@Getter
	private static AdvancementManager advancementManager;

	@Override
	public void onEnable() {
		messagesFile.createNewFile("Loading CustomAdvancements messages.yml",
				"Advancements messages file");
		loadMessages();
		advancementManager = new AdvancementManager(this);
		commandListener = new CommandListener();
		instance = this;
		Objects.requireNonNull(this.getCommand("customadvancements")).setExecutor(commandListener);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		registerAdvancementTypes();
		advancementManager.loadAdvancements();
	}

	private void registerAdvancementTypes() {
		advancementManager.registerAdvancement(new BlockBreak());
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	private void loadMessages() {
		Lang.setFile(messagesFile.getConfig());
		for (final Lang value : Lang.values()) {
			messagesFile.getConfig().addDefault(value.getPath(),
					value.getDefault());
		}

		messagesFile.getConfig().options().copyDefaults(true);
		messagesFile.saveConfig();
	}
}
