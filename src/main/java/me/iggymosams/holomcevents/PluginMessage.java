package me.iggymosams.holomcevents;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessage implements PluginMessageListener {

    HoloMCEvents plugin = api.getPlugin();
    EventManager eventManager = plugin.getEventManager();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        System.out.println("Message Received");
        if (!channel.equalsIgnoreCase("my:events")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if ( subChannel.equalsIgnoreCase("hosting" )) {
            String host = in.readUTF();
            int msgid = in.readInt();
//            eventManager.host = Bukkit.getPlayer(host);
            System.out.println("T");
            eventManager.EventSetup(Bukkit.getPlayer(host));
        } else if ( subChannel.equalsIgnoreCase("iseventrunning")) {
            String request = in.readUTF();
            int msgid = in.readInt();
//            returnEventData(player, "returnEventData", EventManager.getEventType());
        }
        else if ( subChannel.equalsIgnoreCase("joinEvent")) {
            String request = in.readUTF();
            int msgid = in.readInt();
            eventManager.joinEvent(Bukkit.getPlayer(request));
        }
        else {
            String request = in.readUTF();
            int msgid = in.readInt();
        }
    }

    public static void connect(Player player, String server){
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);
        player.sendPluginMessage(api.getPlugin(), "BungeeCord", output.toByteArray());
    }

    public static void sendEventBroadcast( Player player, String channel, String message ){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        out.writeUTF( message );
        out.writeUTF( player.getName() );
        player.sendPluginMessage(api.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void returnEventData( Player player, String channel, String data ){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        out.writeUTF( data );
        //out.writeUTF( player.getName() );
        player.sendPluginMessage(api.getPlugin(), "BungeeCord", out.toByteArray() );
    }
}
