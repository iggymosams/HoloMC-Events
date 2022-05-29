package me.iggymosams.holomcevents;
import me.iggymosams.holomcevents.Games.BlockParty;
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

    public BlockParty blockParty = new BlockParty();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(EventType.equals("Block Party")){
            System.out.println("Block Party Started");
            blockParty.start();
            return true;
        }

            return false;
    }

    public void EventSetup(Player host){
        etgui = Bukkit.createInventory(null, 9, api.color("Event Type"));
        etgui.setItem(1, api.createGuiItem(Material.TNT, "&aBlock Party Event", "&cComing Soon"));
        host.openInventory(etgui);
    }

    public void joinEvent(Player p) {
        if(EventType.equals("Block Party")) {

            blockParty.Join(p);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(!e.getView().getTitle().equals("Event Type")) return;
        e.setCancelled(true);
        if (e.getSlot() == 1) {
            host = p;
            EventType = "Block Party";
            PluginMessage.sendEventBroadcast(p, "_event_broadcast", api.color("Events -> %host% is hosting a %event% event. Do /event to join").replace("%host%", host.getName()).replace("%event%", EventType));

            blockParty.setUp(host);
        }
    }
}
