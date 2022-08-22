package me.tippie.customadvancements;

import lombok.Getter;
import me.tippie.customadvancements.advancement.AdvancementManager;
import me.tippie.customadvancements.advancement.requirement.types.Advancement;
import me.tippie.customadvancements.advancement.requirement.types.Permission;
import me.tippie.customadvancements.advancement.reward.types.ConsoleCommand;
import me.tippie.customadvancements.advancement.reward.types.Message;
import me.tippie.customadvancements.advancement.types.*;
import me.tippie.customadvancements.bstats.Metrics;
import me.tippie.customadvancements.commands.CommandListener;
import me.tippie.customadvancements.player.CAPlayerListener;
import me.tippie.customadvancements.player.CAPlayerManager;
import me.tippie.customadvancements.util.ConfigWrapper;
import me.tippie.customadvancements.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * The main plugin class
 */
public final class CustomAdvancements extends JavaPlugin {


	@Getter @Nullable private static InternalsProvider<?,?,?> internals = null;

	static {
		try {
			final String packageName = CustomAdvancements.class.getPackage().getName();
			final String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			internals = (InternalsProvider<?,?,?>) Class.forName(packageName + "." + internalsName).newInstance();
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException exception) {
			Bukkit.getLogger().log(Level.SEVERE, "CustomAdvancements could not find a valid implementation for this server version.");
		}
	}


	private final String a = getServer().getClass().getPackage().getName();
	private final String version = a.substring(a.lastIndexOf('.') + 1);
	static int ADVANCEMENTS_PER_PACKET = 5;
	static int PROGRESS_PER_PACKET = 5;



	/**
	 * The file that has the messages (messages.yml)
	 */
	private final ConfigWrapper messagesFile = new ConfigWrapper(this, "", "messages.yml");

	/**
	 * {@link me.tippie.customadvancements.CustomAdvancements}
	 */
	@Getter private static CustomAdvancements instance;

	/**
	 * {@link me.tippie.customadvancements.commands.CommandListener}
	 */
	@Getter private static CommandListener commandListener;

	/**
	 * {@link me.tippie.customadvancements.advancement.AdvancementManager}
	 */

	@Getter private static AdvancementManager advancementManager;

	/**
	 * {@link me.tippie.customadvancements.player.CAPlayerManager}
	 */
	@Getter private static CAPlayerManager caPlayerManager;

	/**
	 * The bStats metrics for this plugin
	 */
	private static Metrics metrics;

	@Getter private boolean papiSupport = false;

	/**
	 * Executed on enabling the plugin:
	 * loads the messages, creates the listeners / managers, registers commands and loads data for online players
	 */
	@Override
	public void onEnable() {
		messagesFile.createNewFile("Loading CustomAdvancements messages.yml",
				"Advancements messages file");
		loadMessages();
		saveDefaultConfig();
		reloadConfig();
		advancementManager = new AdvancementManager();
		commandListener = new CommandListener();
		caPlayerManager = new CAPlayerManager();
		instance = this;

		final int pluginId = 10941;
		metrics = new Metrics(this, pluginId);

		this.getCommand("customadvancements").setExecutor(commandListener);
		this.getCommand("customadvancements").setTabCompleter(commandListener);
		getServer().getPluginManager().registerEvents(new CAPlayerListener(), this);

		PROGRESS_PER_PACKET = getConfig().getInt("packet.progress-per-packet", 5);
		getLogger().log(Level.INFO, "Progress per packet is now "+ PROGRESS_PER_PACKET );
		ADVANCEMENTS_PER_PACKET = getConfig().getInt("packet.advancements-per-packet", 5);
		getLogger().log(Level.INFO, "Advancements per packet is now "+ ADVANCEMENTS_PER_PACKET);

		registerAdvancementTypes();
		advancementManager.loadAdvancements();
		for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
			caPlayerManager.loadPlayer(player);
		}
		this.getLogger().log(Level.INFO, "Enabled successfully");

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			papiSupport = true;
			new PAPICustomAdvancementsExpansion().register();
			getLogger().log(Level.INFO, "Hooked into PlaceholderAPI");
		}

		getLogger().log(Level.INFO, "Loading NMS Advancements...");
		if (internals == null) {
			getLogger().log(Level.WARNING, "The minecraft advancements GUI is not supported on this version!");
		} else {
			internals.loadAdvancements(advancementManager.getAdvancementTrees())
					.thenAccept(v -> {
						getLogger().log(Level.INFO, "Advancements Loaded! Sending it to all online players.");
						Bukkit.getOnlinePlayers().parallelStream().forEach(p -> {
							internals.sendAdvancements(p, getConfig().getBoolean("remove-default-trees",true)).exceptionally(e -> {
								CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Could not send advancements to " + p.getName()+ "!",e);
								return null;
							});;
						});
					})
					.exceptionally(throwable -> {
						getLogger().log(Level.SEVERE, "Could not load the minecraft advancements GUI!", throwable);
						return null;
					});
		}
	}

	/**
	 * Registers built-in advancement types.
	 */
	private void registerAdvancementTypes() {
		advancementManager.registerAdvancement(new BlockBreak());
		advancementManager.registerAdvancement(new Join());
		advancementManager.registerAdvancement(new Leave());
		advancementManager.registerAdvancement(new Chat());
		advancementManager.registerAdvancement(new BlockPlace());
		advancementManager.registerAdvancement(new CatchFish());
		advancementManager.registerAdvancement(new Consume());
		advancementManager.registerAdvancement(new Statistic());
		advancementManager.registerAdvancement(new XPChange());
		advancementManager.registerAdvancement(new XPLevelChange());
		advancementManager.registerAdvancement(new DamageTaken());
		advancementManager.registerAdvancement(new DamageDealt());
		advancementManager.registerAdvancement(new KillEntity());
		advancementManager.registerAdvancement(new ShearSheep());
		advancementManager.registerAdvancement(new BreedEntity());
		advancementManager.registerAdvancement(new ExecuteCommand());
		advancementManager.registerAdvancement(new BlocksTravelled());
		advancementManager.registerAdvancement(new Playtime());
		advancementManager.registerAdvancement(new CraftItem());
		advancementManager.registerAdvancement(new Smelting());
		advancementManager.registerAdvancement(new Taming());
		advancementManager.registerAdvancement(new Enchant());
		advancementManager.registerAdvancement(new RideEntity());
		advancementManager.registerAdvancement(new BrewPotion());
		advancementManager.registerAdvancement(new Impossible());
		advancementManager.registerAdvancement(new ObtainItem());

		if (version.matches("(?i)v1_16+|v1_17+|v1_18+"))
			advancementManager.registerAdvancement(new Harvest());

		if (version.matches("(?i)v1_14_R2|v1_15_R1|v1_16+|v1_17+|v1_18+"))
			advancementManager.registerAdvancement(new RaidFinish());

		if (getServer().getPluginManager().getPlugin("Essentials") != null) {
			advancementManager.registerAdvancement(new Money());
		}

		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			advancementManager.registerAdvancement(new RegionStay());
		}
		advancementManager.registerAdvancementReward(new Message());
		advancementManager.registerAdvancementReward(new ConsoleCommand());

		advancementManager.registerAdvancementRequirement(new Advancement());
		advancementManager.registerAdvancementRequirement(new Permission());
	}


	/**
	 * Executed when plugin disables:
	 * saves and unloads all online players.
	 */
	@Override
	public void onDisable() {
		for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.getOpenInventory().close();
			caPlayerManager.savePlayer(player);
			caPlayerManager.unloadPlayer(player);
		}
		advancementManager.unregisterAll();
		advancementManager = null;
		commandListener = null;
		caPlayerManager = null;
		Bukkit.getScheduler().cancelTasks(this);
		this.getLogger().log(Level.INFO, "Disabled successfully");
	}


	public void onReload() {
		for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.getOpenInventory().close();
			caPlayerManager.savePlayer(player);
			caPlayerManager.unloadPlayer(player);
		}

		reloadConfig();
		messagesFile.reloadConfig();
		loadMessages();

		PROGRESS_PER_PACKET = getConfig().getInt("packet.progress-per-packet", 5);
		getLogger().log(Level.INFO, "Progress per packet is now "+ PROGRESS_PER_PACKET );
		ADVANCEMENTS_PER_PACKET = getConfig().getInt("packet.advancements-per-packet", 5);
		getLogger().log(Level.INFO, "Advancements per packet is now "+ ADVANCEMENTS_PER_PACKET);

		if (!papiSupport && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			papiSupport = true;
			new PAPICustomAdvancementsExpansion().register();
			getLogger().log(Level.INFO, "Hooked into PlaceholderAPI");
		}

		advancementManager.loadAdvancements();

		for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
			caPlayerManager.loadPlayer(player);
		}

		getLogger().log(Level.INFO, "Loading NMS Advancements...");
		if (internals == null) {
			getLogger().log(Level.WARNING, "The minecraft advancements GUI is not supported on this version!");
		} else {
			internals.loadAdvancements(advancementManager.getAdvancementTrees())
					.thenAccept(v -> {
						getLogger().log(Level.INFO, "Advancements Loaded! Sending it to all online players.");
						Bukkit.getOnlinePlayers().parallelStream().forEach(p -> {
							internals.sendAdvancements(p, getConfig().getBoolean("remove-default-trees",true)).exceptionally(e -> {
								CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Could not send advancements to " + p.getName()+ "!",e);
								return null;
							});;
						});
					})
					.exceptionally(throwable -> {
						getLogger().log(Level.SEVERE, "Could not load the minecraft advancements GUI!", throwable);
						return null;
					});
		}
	}

	/**
	 * Loads and enables messages.yml
	 */
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
