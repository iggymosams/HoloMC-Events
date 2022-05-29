package me.iggymosams.holomcevents;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HoloMCEvents extends JavaPlugin {

    private static HoloMCEvents plugin;

    EventManager eventManager = new EventManager();

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getMessenger().registerOutgoingPluginChannel( this, "BungeeCord" ); 
        getServer().getMessenger().registerIncomingPluginChannel( this, "my:events", new PluginMessage() ); 
        
        RegisterCommands();
        RegisterEvents();
    }

    private void RegisterCommands() {
        getCommand("start").setExecutor(eventManager);
    }

    private void RegisterEvents() {
        getLogger().info("Registering Events");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(eventManager, this);
        pm.registerEvents(eventManager.blockParty, this);
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
