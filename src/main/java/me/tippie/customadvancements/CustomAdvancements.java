package me.tippie.customadvancements;

import me.tippie.customadvancements.commands.CommandListener;
import me.tippie.utils.ConfigWrapper;
import me.tippie.utils.Lang;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomAdvancements extends JavaPlugin {
	private final ConfigWrapper messagesFile = new ConfigWrapper(this, "", "messages.yml");

	@Override
	public void onEnable() {
		messagesFile.createNewFile("Loading CustomAdvancements messages.yml",
				"Advancements messages file");

		loadMessages();
		Objects.requireNonNull(this.getCommand("customadvancements")).setExecutor(new CommandListener());

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
