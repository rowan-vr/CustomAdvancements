package me.tippie.customadvancements;

import lombok.Getter;
import me.tippie.customadvancements.commands.CommandListener;
import me.tippie.utils.ConfigWrapper;
import me.tippie.utils.Lang;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomAdvancements extends JavaPlugin {
	private final ConfigWrapper messagesFile = new ConfigWrapper(this, "", "messages.yml");
	@Getter
	private static CustomAdvancements instance;
	@Getter
	private static CommandListener commandListener;
	@Override
	public void onEnable() {
		messagesFile.createNewFile("Loading CustomAdvancements messages.yml",
				"Advancements messages file");

		loadMessages();
		commandListener = new CommandListener();
		instance = this;
		Objects.requireNonNull(this.getCommand("customadvancements")).setExecutor(commandListener);

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
