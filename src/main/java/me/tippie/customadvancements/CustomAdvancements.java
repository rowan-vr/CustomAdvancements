package me.tippie.customadvancements;

import me.tippie.customadvancements.commands.CommandListener;
import me.tippie.utils.ConfigWrapper;
import me.tippie.utils.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomAdvancements extends JavaPlugin {
	private final ConfigWrapper messagesFile = new ConfigWrapper(this, "", "messages.yml");

	@Override
	public void onEnable() {
		messagesFile.createNewFile("Loading CustomAdvancements messages.yml",
				"EZAdvancementsLite messages file");

		loadMessages();
		this.getCommand("customadvancements").setExecutor(new CommandListener(this));

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
