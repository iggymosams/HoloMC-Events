package me.iggymosams.holomcevents;
import me.iggymosams.holomcevents.Games.BlockParty;
import me.iggymosams.holomcevents.Games.TNTTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class EventManager implements Listener, CommandExecutor {
    Inventory etgui;

    public String EventType = null;
    public Player host = null;

    public BlockParty blockParty;
    public TNTTag tntTag;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (EventType) {
            case "Block Party":
                System.out.println("Block Party Started");
                blockParty.start();
                return true;
            case "TNT Tag":
                tntTag.start();
                return true;
        }

            return false;
    }

    public void EventSetup(Player host){
        etgui = Bukkit.createInventory(null, 9, api.color("Event Type"));
        etgui.setItem(1, api.createGuiItem(Material.PINK_CONCRETE, "&aBlock Party Event", "&cComing Soon"));
        etgui.setItem(2, api.createGuiItem(Material.TNT, "&aTNT Tag Event", "&cComing Soon"));
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
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(!e.getView().getTitle().equals("Event Type")) return;
        e.setCancelled(true);

        switch (e.getSlot()) {
            case 1:
                host = p;
                EventType = "Block Party";
                blockParty.setUp(host);
                break;
            case 2:
                host = p;
                EventType = "TNT Tag";
                tntTag.setUp(host);
                break;
        }
        PluginMessage.sendEventBroadcast(p, "_event_broadcast", api.color("Events -> %host% is hosting a %event% event. Do /event to join").replace("%host%", host.getName()).replace("%event%", EventType));
    }
}
