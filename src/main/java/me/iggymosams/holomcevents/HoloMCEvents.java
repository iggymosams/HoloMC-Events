package me.iggymosams.holomcevents;

import me.iggymosams.holomcevents.Games.BlockParty;
import me.iggymosams.holomcevents.Games.FloorIsLava;
import me.iggymosams.holomcevents.Games.TNTTag;
import me.iggymosams.holomcevents.Games.UHC;
import me.iggymosams.holomcevents.Util.MessagesConfig;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HoloMCEvents extends JavaPlugin {

    private static HoloMCEvents plugin;

    EventManager eventManager = new EventManager();
    MessagesConfig messagesConfig;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getMessenger().registerOutgoingPluginChannel( this, "BungeeCord" ); 
        getServer().getMessenger().registerIncomingPluginChannel( this, "my:events", new PluginMessage() ); 

        RegisterGames();
        RegisterCommands();
        RegisterEvents();
        LoadMessages();
    }

    private void LoadMessages() {
        messagesConfig = new MessagesConfig();
        messagesConfig.setup();
        messagesConfig.loadDefaults();
        messagesConfig.get().options().copyDefaults(true);
        messagesConfig.save();
    }

    private void RegisterGames() {
        getLogger().info("Registering Games");
        eventManager.blockParty = new BlockParty();
        eventManager.tntTag = new TNTTag();
        eventManager.uhc = new UHC();
        eventManager.floorIsLava = new FloorIsLava();
    }

    private void RegisterCommands() {
        getLogger().info("Registering Commands");
        getCommand("start").setExecutor(eventManager);
    }

    private void RegisterEvents() {
        getLogger().info("Registering Events");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(eventManager, this);
        pm.registerEvents(eventManager.blockParty, this);
        pm.registerEvents(eventManager.tntTag, this);
        pm.registerEvents(eventManager.uhc, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static HoloMCEvents getPluginMain(){
        return plugin;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
