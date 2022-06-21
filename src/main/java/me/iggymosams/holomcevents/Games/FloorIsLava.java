package me.iggymosams.holomcevents.Games;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import me.iggymosams.holomcevents.HoloMCEvents;
import me.iggymosams.holomcevents.api;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class FloorIsLava {

    HoloMCEvents plugin = api.getPlugin();

    public List<Player> players = new ArrayList<>();

    Player host;

    String worldName = "FloorIsLava";
    World world;
    WorldBorder worldBorder;

    int worldSize = 100;
    int lavaTime = 8;
    int graceLevel = 62;
    boolean grace = true;
    boolean maxHeight = false;


    MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    MVWorldManager worldManager = core.getMVWorldManager();

    public boolean allowJoining = false;

    int taskID = 1;

    public void setUp(Player host) {
        plugin = api.getPlugin();
        players.clear();

        this.host = host;
        
        api.sendEventMessage(host, "WorldGenerating");

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
                api.sendEventMessage(p, "AlreadyInEvent");
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

    private void game() {

        Location edgeMax = world.getSpawnLocation().clone().add(worldSize/2D,0,worldSize/2D);
        Location edgeMin = world.getSpawnLocation().clone().subtract(worldSize/2D,0,worldSize/2D);;

        edgeMin.setY(-65);
        edgeMax.setY(-64);

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            float time = 0;
            @Override
            public void run() {

                if (time % lavaTime == 0) {

                    if (edgeMax.getY() == 256) {
                        maxHeight = true;
                        api.eventBroadcast("Deathmatch Started");
                        deathmatch();
                        Bukkit.getScheduler().cancelTask(taskID);
                    }
                    if (!maxHeight) {
                        for (int x = edgeMin.getBlockX(); x <= edgeMax.getBlockX(); x++) {
                            for (int y = edgeMin.getBlockY(); y <= edgeMax.getBlockY(); y++) {
                                for (int z = edgeMin.getBlockZ(); z <= edgeMax.getBlockZ(); z++) {
                                    Block block = new Location(world, x, y, z).getBlock();
                                    if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                                        block.setType(Material.LAVA);
                                    }
                                }
                            }
                        }

                        if (edgeMax.getY() > 62) {
                            grace = false;
                            //TODO: ADD RANDOM CHANCE EVENTS TO KEEP GAME INTERESTING
                        }
                        edgeMax.setY(edgeMax.getY() + 1);
                        edgeMin.setY(edgeMin.getY() + 1);
                    }
                }
                System.out.println(time/lavaTime + "%");
                if(!maxHeight){
                    if(time == lavaTime) {
                        time = 0;
                    }
                }

                if(players.size() == 1){
                    endGame();
                }
                time ++;
            }
        }, 0, 20);
    }

    private void endGame() {
        Bukkit.getScheduler().cancelTask(taskID);
        api.eventBroadcast(api.getMessage("EventWin").replace("%player%", players.get(0).getName()));
        players.clear();
        allowJoining = true;
        worldManager.deleteWorld(worldName);
        Bukkit.getScheduler().runTaskLater(plugin, api::returnPlayers, 3*20);
    }

    private void deathmatch() {
        worldBorder.setSize(1, 180);
        //TODO: ADD RANDOM CHANCE EVENT
    }

    private void playerDeath(Player p) {
        if(grace) {
            return;
        }
        players.remove(p);
        api.eventBroadcast("&c%player% Has Died %playersleft% Players Remain".replace("%player%", p.getName()).replace("%playersleft%", String.valueOf(players.size())));
        p.spigot().respawn();
        p.teleport(world.getSpawnLocation());
        p.setGameMode(GameMode.SPECTATOR);
        if(players.size() == 1){
            endGame();
        }
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

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(players.contains(p)){
            players.remove(p);
        }
    }
}
