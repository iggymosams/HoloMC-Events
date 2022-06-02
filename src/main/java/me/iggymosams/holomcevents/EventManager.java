package me.iggymosams.holomcevents;

import me.iggymosams.holomcevents.Games.BlockParty;
import me.iggymosams.holomcevents.Games.FloorIsLava;
import me.iggymosams.holomcevents.Games.TNTTag;
import me.iggymosams.holomcevents.Games.UHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class EventManager implements Listener, CommandExecutor {
    Inventory etgui;

    public String EventType = null;
    public Player host = null;

    public BlockParty blockParty;
    public TNTTag tntTag;
    public UHC uhc;
    public FloorIsLava floorIsLava;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (host == p) {
                switch (EventType) {
                    case "Block Party":
                        blockParty.start();
                        return true;
                    case "TNT Tag":
                        tntTag.start();
                        return true;
                    case "UHC":
                        uhc.start();
                        return true;
                    case "Floor Is Lava":
                        floorIsLava.start();
                        return true;
                }
                api.sendEventMessage(p, "EventStart");
            } else {
                api.noPermission(p);
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public void EventSetup(Player host) {
        etgui = Bukkit.createInventory(null, 9, api.color("Event Type"));
        etgui.setItem(1, api.createGuiItem(Material.PINK_CONCRETE, "&aBlock Party Event", "&cComing Soon"));
        etgui.setItem(2, api.createGuiItem(Material.TNT, "&aTNT Tag Event", "&cComing Soon"));
        etgui.setItem(3, api.createGuiItem(Material.GOLDEN_APPLE, "&aUHC Event", "&cComing Soon"));
        etgui.setItem(4, api.createGuiItem(Material.LAVA_BUCKET, "&aFloor Is Lava Event", "&cComing Soon"));
        host.openInventory(etgui);
    }

    public void joinEvent(Player p) {
        switch (EventType) {
            case "Block Party":
                blockParty.join(p);
                break;
            case "TNT Tag":
                tntTag.join(p);
                break;
            case "UHC":
                uhc.join(p);
                break;
            case "Floor Is Lava":
                floorIsLava.join(p);
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().equals("Event Type"))
            return;
        e.setCancelled(true);
        host = p;
        switch (e.getSlot()) {
            case 1:
                EventType = "Block Party";
                blockParty.setUp(host);
                break;
            case 2:
                EventType = "TNT Tag";
                tntTag.setUp(host);
                break;
            case 3:
                EventType = "UHC";
                p.closeInventory();
                uhc.setUp(host);
                break;
            case 4:
                EventType = "Floor Is Lava";
                p.closeInventory();
                floorIsLava.setUp(host);
                break;
        }
        PluginMessage.sendEventBroadcast(p, "_event_broadcast", api.getPrefix() + " "
                + api.getMessage("EventHost").replace("%host%", host.getName()).replace("%eventtype%", EventType));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().teleport(new Location(Bukkit.getWorld("spawn"), 0,63,0));
    }
}
