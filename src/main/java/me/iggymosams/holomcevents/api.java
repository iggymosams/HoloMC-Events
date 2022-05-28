package me.iggymosams.holomcevents;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
}
