package eu.levenproxy.netcache.client;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.DisconnectEvent;
import com.esotericsoftware.kryonetty.network.ReceiveEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import eu.levenproxy.netcache.packets.request.PacketRequestRegisterSession;

public class NetCacheClientListener implements NetworkListener {

    private final NetCacheClient netCacheClient;

    public NetCacheClientListener(NetCacheClient netCacheClient) {
        this.netCacheClient = netCacheClient;
    }

    @NetworkHandler
    public void onConnect(ConnectEvent event) {
        netCacheClient.getLogger().info("Successful connected to the server! Attempting session-registration...");
        event.getCtx().channel().writeAndFlush(new PacketRequestRegisterSession(netCacheClient.getMemberName(), netCacheClient.getSessionId()));
    }

    @NetworkHandler
    public void onDisconnect(DisconnectEvent event) {
        netCacheClient.getLogger().info("Disconnected.");
    }

    @NetworkHandler
    public void onReceive(ReceiveEvent event) {
        netCacheClient.process().receivedObject(event.getCtx(), event.getObject());
    }
}
