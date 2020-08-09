package eu.levenproxy.netcache.client;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.DisconnectEvent;
import com.esotericsoftware.kryonetty.network.ReceiveEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import eu.levenproxy.netcache.packets.request.PacketRequestRegisterSession;

public class ClientListener implements NetworkListener {

    private final CacheClient cacheClient;

    public ClientListener(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    @NetworkHandler
    public void onConnect(ConnectEvent event) {
        cacheClient.getLogger().info("Successful connected to the server! Attempting session-registration...");
        event.getCtx().channel().writeAndFlush(new PacketRequestRegisterSession(cacheClient.getMemberName(), cacheClient.getSessionId()));
    }

    @NetworkHandler
    public void onDisconnect(DisconnectEvent event) {
        cacheClient.getLogger().info("Disconnected.");
    }

    @NetworkHandler
    public void onReceive(ReceiveEvent event) {
        cacheClient.process().receivedObject(event.getCtx(), event.getObject());
    }
}
