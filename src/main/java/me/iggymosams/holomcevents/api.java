package me.iggymosams.holomcevents;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class api {

    public static HoloMCEvents getPlugin(){
        return HoloMCEvents.getPluginMain();
    }

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(color(name));
        List<String> itemlore = Arrays.asList(lore);
        for (int counter = 0; counter < itemlore.size(); counter++) {
            itemlore.set(counter, color(itemlore.get(counter)));
        }

        meta.setLore(itemlore);

        item.setItemMeta(meta);

        return item;
    }

    public static void eventBroadcast(String msg) {
        Bukkit.broadcastMessage(api.color(getPrefix() + msg));
    }

    public static void returnPlayers() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.getInventory().clear();
            PluginMessage.connect(p, "lobby");
        }
    }

    public static String getMessage(String path) {
        HoloMCEvents plugin = api.getPlugin();
        return api.color(plugin.messagesConfig.get().getString(path));
    }

    public static void noPermission(Player p){
        p.sendMessage(api.getMessage("NoPermission"));
    }

    public static String getPrefix() {
        return api.getMessage("EventPrefix");
    }

    public static void sendEventMessage(Player p, String path){
        p.sendMessage(api.getPrefix() + " " + api.getMessage(path));
    }
}
