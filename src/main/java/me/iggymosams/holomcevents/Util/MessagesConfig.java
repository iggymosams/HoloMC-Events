package me.iggymosams.holomcevents.Util;

import me.iggymosams.holomcevents.HoloMCEvents;
import me.iggymosams.holomcevents.api;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {

    HoloMCEvents plugin;

    private File messageConfigFile;
    private FileConfiguration messageConfig;

    public void setup() {
        plugin = api.getPlugin();
        messageConfigFile = new File(plugin.getDataFolder(), "messages.yml");

        if(!messageConfigFile.exists()) {
            try {
                messageConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messageConfig = YamlConfiguration.loadConfiguration(messageConfigFile);
    }

    public FileConfiguration get() {
        return messageConfig;
    }

    public void save() {
        try {
            messageConfig.save(messageConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        messageConfig = YamlConfiguration.loadConfiguration(messageConfigFile);
    }

    public void loadDefaults() {
        messageConfig.addDefault("EventPrefix","&c&lEVENTS ➥");
        messageConfig.addDefault("NoPermission","&cYou don't have permission");
        messageConfig.addDefault("EventHost","&6&l%host% &6is hosting a &6&l%eventtype% &6event. Do &a&l/event &6to join");
        messageConfig.addDefault("EventStart","You have started the event");
        messageConfig.addDefault("EventWin","&6&l%player% has won the event!");
        messageConfig.addDefault("WorldGenerating", "&fThe world is generating. Please Wait. The server might lag")
        messageConfig.addDefault("AlreadyInEvent", "&fYou are already in this event");
        messageConfig.addDefault("BlockPartyFloorTimer","The Timer has been Shortened");
    }
}
