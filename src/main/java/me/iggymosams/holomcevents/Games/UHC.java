package me.iggymosams.holomcevents.Games;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import me.iggymosams.holomcevents.HoloMCEvents;
import me.iggymosams.holomcevents.api;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class UHC implements Listener {

    HoloMCEvents plugin = api.getPlugin();

    public List<Player> players = new ArrayList<>();

    Player host;

    String worldName = "UHC";
    World world;
    WorldBorder worldBorder;

    int graceTime = 60;
    boolean grace;

    int worldTime = 300;
    int worldSize = 2500;
    int shrinkSize = 100;
    int shrinkTime = 300;

    MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    MVWorldManager worldManager = core.getMVWorldManager();

    public boolean allowJoining = false;

    int taskID = 1;

    public void setUp(Player host) {
        plugin = api.getPlugin();
        players.clear();

        this.host = host;

        host.sendMessage(api.color("The world is generating. Please Wait. The server might lag"));

        worldManager.addWorld(
                worldName,
                World.Environment.NORMAL,
                null,
                WorldType.NORMAL,
                true,
                null
        );

        world = Bukkit.getWorld(worldName);

        MultiverseWorld mvWorld = worldManager.getMVWorld(worldName);
        mvWorld.allowPortalMaking(AllowedPortalType.NONE);

        worldBorder = world.getWorldBorder();
        worldBorder.setCenter(world.getSpawnLocation());
        worldBorder.setSize(5);

        grace = true;
        allowJoining = true;
        join(host);
    }

    public void join(Player p) {
        if(allowJoining) {
            if (!players.contains(p)) {
                p.teleport(world.getSpawnLocation());
                p.setGameMode(GameMode.ADVENTURE);
                p.setHealth(20);
                p.setFoodLevel(20);
                players.add(p);
            } else {
                p.sendMessage("You are already in this event");
            }
        }
    }

    public void start() {
        allowJoining = false;
        for(Player p : players) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
        }
        worldBorder.setSize(worldSize);
        game();
    }

    //Main Game Loop
    public void game() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int time = 0;
            @Override
            public void run() {
                System.out.println(time);
                if(time == graceTime) {
                    grace = false;
                    api.eventBroadcast("The grace period has ended");
                }
                if(time == worldTime) {
                    shrinkBorder();
                }
                time++;
            }
        },0, 20);
    }

    private void shrinkBorder() {
        worldBorder.setSize(shrinkSize, shrinkTime);
        api.eventBroadcast("The world border is shrinking");
    }

    private void playerDeath(Player p) {
        players.remove(p);
        if(players.size() == 1) {
            endGame();
        }
        p.setGameMode(GameMode.SPECTATOR);
        p.getInventory().clear();
        p.spigot().respawn();
    }

    private void endGame() {
        Bukkit.getScheduler().cancelTask(taskID);
        api.eventBroadcast(api.getMessage("EventWin").replace("%player%", players.get(0).getName()));
        players.clear();
        allowJoining = true;
        worldManager.deleteWorld(worldName);
        Bukkit.getScheduler().runTaskLater(plugin, api::returnPlayers, 3*20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if(!players.contains(p)) return;
        if(p.getWorld() != world) return;
        playerDeath(p);
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!(e.getDamager() instanceof Player)) return;
        if(e.getEntity().getWorld() != world) return;
        if(grace) {
            e.setCancelled(true);
        }
    }
}
