package me.iggymosams.holomcevents.Games;

import me.iggymosams.holomcevents.HoloMCEvents;
import me.iggymosams.holomcevents.PluginMessage;
import me.iggymosams.holomcevents.api;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TNTTag implements Listener {

    HoloMCEvents plugin = api.getPlugin();

    public List<Player> players = new ArrayList<>();
    public List<Player> taggers = new ArrayList<>();

    Player host;

    String worldName = "tnttag";
    World world = Bukkit.getWorld(worldName);
    WorldBorder worldBorder = world.getWorldBorder();

    Location spawn = new Location(world, 0,64, 0);
    Team playerTeam;
    Team taggerTeam;

    int taskID = 1;
    int tntTime = 30;

    public boolean allowJoining = false;

    public void setUp(Player host) {
        plugin = api.getPlugin();
        players.clear();
        taggers.clear();

        this.host = host;

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setTime(6000);

        worldBorder.setCenter(world.getSpawnLocation());
        worldBorder.setSize(7);

        setupTeams();

        allowJoining = true;
        join(host);
    }

    private void setupTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        System.out.println(scoreboard.getTeams());
        if(scoreboard.getTeam("tntplayers") == null) {
            playerTeam = scoreboard.registerNewTeam("tntplayers");
            playerTeam.color(NamedTextColor.WHITE);
        } else {
            playerTeam = scoreboard.getTeam("tntplayers");
        }
        if(scoreboard.getTeam("tnttaggers") == null) {
            taggerTeam = scoreboard.registerNewTeam("tnttaggers");
            taggerTeam.color(NamedTextColor.RED);
            taggerTeam.setPrefix("[TNT] ");
        } else {
            taggerTeam = scoreboard.getTeam("tnttaggers");
        }
    }

    public void join(Player p) {
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

    public void start() {
        allowJoining = false;
        for(Player p : players) {
            p.setHealth(20);
            p.setFoodLevel(20);
            playerTeam.addEntry(p.getName());
        }

        worldBorder.setSize(25);
        Bukkit.getScheduler().runTaskLater(plugin, this::pickTaggers, 2*20);
    }

    //TODO: Make tagger team size bigger by player count
    private void pickTaggers() {
        taggers.clear();
        Random random = new Random();
        int index = random.nextInt(players.size());
        taggers.add(players.get(index));
        taggerTeam.addEntry(players.get(index).getName());

        for (Player p : taggers) {
            setTaggerInv(p);
        }
        doTNTTag();
    }

    private void setTaggerInv(Player p) {
        for (int i = 0; i < 8; i++) {
            p.getInventory().setItem(i, new ItemStack(Material.TNT));
        }
        p.getInventory().setHelmet(new ItemStack(Material.TNT));
    }

    //Main Game Loop
    public void doTNTTag() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int time = 0;
            @Override
            public void run() {
                if(time == tntTime) {
                    time = 0;
                    explode();
                    Bukkit.getScheduler().cancelTask(taskID);
                }
                time++;
            }
        }, 0L, 20L);
    }

    //Explode all Taggers
    private void explode() {
        for (Player p : taggers) {
            players.remove(p);
            taggerTeam.removeEntry(p.getName());
            p.setGameMode(GameMode.SPECTATOR);
            world.createExplosion(p.getLocation(), 0);
        }
        taggers.clear();
        checkPlayers();
        if(players.size() != 1) {
            Bukkit.getScheduler().runTaskLater(plugin, this::pickTaggers, 5 * 20);
        }
    }

    private void checkPlayers() {
        if(players.size() == 1) {
            endGame();
        }
    }

    private void endGame() {
        Bukkit.getScheduler().cancelTask(taskID);
        api.eventBroadcast(players.get(0).getName() + " has won the event!");
        Bukkit.getScheduler().runTaskLater(plugin, this::returnPlayers, 3*20);
    }

    public void returnPlayers() {
        for(Player p : Bukkit.getOnlinePlayers()){
            PluginMessage.connect(p, "lobby");
        }
        players.clear();
        taggers.clear();
        allowJoining = true;
    }

    private void playerTagged(Player player, Player tagger) {
        taggers.remove(tagger);
        taggers.add(player);

        tagger.getInventory().clear();
        setTaggerInv(player);
        taggerTeam.removeEntry(tagger.getName());
        taggerTeam.addEntry(player.getName());

        playerTeam.removeEntry(player.getName());
        playerTeam.addEntry(tagger.getName());

        api.eventBroadcast("%tagger% has tagged %player%".replace("%tagger%", tagger.getName()).replace("%player%", player.getName()));
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if(p.getWorld() != world) return;
        System.out.println("TNT");
        if(players.contains(p)) {
//            p.setHealth(20);
            if(taggers.contains(e.getDamager())) {
                   playerTagged(p, (Player) e.getDamager());
            }
        }
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
        if(taggers.contains(p)){
            if(taggers.size() == 1) {
                Bukkit.getScheduler().cancelTask(taskID);
                explode();
            }
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
//        e.setCancelled(true);
        ((Player) e.getEntity()).setHealth(20);
    }
}
