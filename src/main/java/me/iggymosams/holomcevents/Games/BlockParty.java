package me.iggymosams.holomcevents.Games;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.iggymosams.holomcevents.EventManager;
import me.iggymosams.holomcevents.HoloMCEvents;
import me.iggymosams.holomcevents.PluginMessage;
import me.iggymosams.holomcevents.api;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockParty implements Listener {

    HoloMCEvents plugin = api.getPlugin();
//    EventManager eventManager = plugin.getEventManager();

    public List<Player> players = new ArrayList<>();

    Location plusCorner;
    Location minusCorner;
    Location spawn;

    MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    MVWorldManager worldManager = core.getMVWorldManager();

    Player host;

    String worldName = "BlockParty";
    World world = Bukkit.getWorld(worldName);

    int mapSize = 25;
    
    Team team;

    List<Material> colors = new ArrayList<>();

    int taskID = 1;

    int defaultTime = 5;
    int time = defaultTime;
    int modifier = 5;
    int level = 0;
    int floorCount = 1;

    public boolean allowJoining = false;

    public void setUp(Player host) {

        plugin = api.getPlugin();

        players.clear();

        this.host = host;

        spawn = new Location(world, 0, 64,0);

        minusCorner = spawn.clone().subtract(mapSize/2D,0,mapSize/2D);
        plusCorner = spawn.clone().add(mapSize/2D,0,mapSize/2D);

        plusCorner.setY(63);
        minusCorner.setY(63);
//        setupTeam();
        setupColors();
        generatePlatform(plusCorner, minusCorner);
        allowJoining = true;
        join(host);
    }

    private void setupColors() {
        colors.add(Material.ORANGE_CONCRETE);
        colors.add(Material.MAGENTA_CONCRETE);
        colors.add(Material.LIGHT_BLUE_CONCRETE);
        colors.add(Material.YELLOW_CONCRETE);
        colors.add(Material.LIME_CONCRETE);
        colors.add(Material.PINK_CONCRETE);
        colors.add(Material.CYAN_CONCRETE);
        colors.add(Material.BLUE_CONCRETE);
        colors.add(Material.PURPLE_CONCRETE);
        colors.add(Material.RED_CONCRETE);
        colors.add(Material.GREEN_CONCRETE);
        colors.add(Material.BLACK_CONCRETE);
        colors.add(Material.WHITE_CONCRETE);
    }

    //TODO: Fix :)
    private void setupTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        if(!scoreboard.getTeams().contains("BlockParty")) {
            team = scoreboard.registerNewTeam("BlockParty");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    //Main Game Loop
    private void game() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public Material chosen = Material.WHITE_CONCRETE;
            public float floorTime = 0;
            public int emptyTime = 2;
            public boolean floorState = false;

            @Override
            public void run() {
                checkPlayers();
                if (floorState) {
                    if (floorTime == (time - level)) {
                        floorState = false;
                        floorTime = 0;
                        removePlatform(chosen, plusCorner, minusCorner);
                    }

                    floorTime++;
                }
                if (!floorState) {
                    if (emptyTime == 3) {
                        chosen = generatePattern(plusCorner, minusCorner);
                        floorState = true;
                        emptyTime = 0;
                    }
                    emptyTime++;
                }

            }
        }, 0L, 20L);
    }

    //Generates white starting platform
    private void generatePlatform(Location plusCorner, Location minusCorner) {
        for (int x = minusCorner.getBlockX(); x <= plusCorner.getBlockX(); x++) {
            for (int y = minusCorner.getBlockY(); y <= plusCorner.getBlockY(); y++) {
                for (int z = minusCorner.getBlockZ(); z <= plusCorner.getBlockZ(); z++) {
                    Block block = new Location(world, x, y, z).getBlock();
                    block.setType(Material.WHITE_CONCRETE);
                }
            }
        }
    }

    //Generates Random Pattern
    public Material generatePattern(Location plusCorner, Location minusCorner) {
        if (floorCount % modifier == 0) {
            if (level <= 2) {
                level++;
                for (Player p : players) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }
        }
        floorCount++;
        Material chosen = Material.WHITE_CONCRETE;
        for (int x = minusCorner.getBlockX(); x <= plusCorner.getBlockX(); x++) {
            for (int y = minusCorner.getBlockY(); y <= plusCorner.getBlockY(); y++) {
                for (int z = minusCorner.getBlockZ(); z <= plusCorner.getBlockZ(); z++) {
                    Block block = new Location(world, x, y, z).getBlock();
                    Random random = new Random();
                    int color = random.nextInt(colors.size());
                    block.setType(colors.get(color));
                    chosen = colors.get(color);

                    for (Player p : players) {
                        p.getInventory().setItem(4, new ItemStack(colors.get(color)));
                    }
                }
            }
        }
        checkPlayers();
        return chosen;
    }

    //Removes the platform except chosen color
    public void removePlatform(Material chosen, Location plusCorner, Location minusCorner) {
        for (int x = minusCorner.getBlockX(); x <= plusCorner.getBlockX(); x++) {
            for (int y = minusCorner.getBlockY(); y <= plusCorner.getBlockY(); y++) {
                for (int z = minusCorner.getBlockZ(); z <= plusCorner.getBlockZ(); z++) {
                    Block block = new Location(world, x, y, z).getBlock();
                    if (block.getType() != chosen) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
        checkPlayers();
    }

    public void start() {
        allowJoining = false;
        for(Player p : players) {
            p.teleport(new Location(world, 0, 64, 0));
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
//            team.addEntry(p.getName());
        }
        Bukkit.getScheduler().runTaskLater(plugin, this::game, 3*20);
    }

    private void checkPlayers() {
        if (players.size() == 1) {
            endGame();
        }
    }

    private void endGame() {
        Bukkit.getScheduler().cancelTask(taskID);
        generatePlatform(plusCorner, minusCorner);
        api.eventBroadcast(players.get(0).getName() + " has won the event!");
        Bukkit.getScheduler().runTaskLater(plugin, this::returnPlayers, 3*20);
    }

    public void returnPlayers() {
        for(Player p : Bukkit.getOnlinePlayers()){
            PluginMessage.connect(p, "lobby");
        }
        players.clear();
        allowJoining = true;
    }

    public void join(Player p) {
        System.out.println(p);
        if(allowJoining) {
            if (!players.contains(p)) {
                p.teleport(spawn);
                p.setGameMode(GameMode.ADVENTURE);
                p.setHealth(20);
                p.setFoodLevel(20);
                players.add(p);
            } else {
                p.sendMessage("You are already in this event");
            }
        }
    }

    private void playerDeath(Player p) {
        players.remove(p);
        p.getInventory().clear();
        p.teleport(spawn);
        p.setGameMode(GameMode.SPECTATOR);
        checkPlayers();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(p.getWorld() != world) return;

        if(p.getLocation().getY() <= 54) {
            if(players.contains(p)) {
                playerDeath(p);
            } else {
                p.teleport(spawn);
            }
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!(e.getDamager() instanceof Player)) return;
        if(e.getEntity().getWorld() != world) return;
        System.out.println("Test");
        if(players.contains(e.getEntity())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(p.getWorld() != world) return;
        if(!p.hasPermission("events.build")) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(p.getWorld() != world) return;
        if(!p.hasPermission("events.build")) e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(players.contains(p)){
            players.remove(p);
        }
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        if(e.getEntity().getWorld() != world) return;
        e.setCancelled(true);
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(e.getEntity().getWorld() != world) return;
        e.setCancelled(true);
        ((Player) e.getEntity()).setHealth(20);
    }
}
