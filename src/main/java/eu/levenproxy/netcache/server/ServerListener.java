package eu.levenproxy.netcache.server;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.DisconnectEvent;
import com.esotericsoftware.kryonetty.network.ReceiveEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import eu.levenproxy.netcache.packets.request.*;
import eu.levenproxy.netcache.packets.response.*;
import eu.levenproxy.netcache.utils.Utils;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServerListener implements NetworkListener {

    private final CacheServer cacheServer;

    public ServerListener(CacheServer cacheServer) {
        this.cacheServer = cacheServer;
    }

    @NetworkHandler
    public void onConnect(ConnectEvent event) {
        cacheServer.getLogger().info("CLIENT | " + Utils.wrapInetAddress(event.getCtx()) + " <> connected..");
    }

    @NetworkHandler
    public void onDisconnect(DisconnectEvent event) {
        cacheServer.getLogger().info("CLIENT | " + Utils.wrapInetAddress(event.getCtx()) + " <> disconnected..");
    }

    @NetworkHandler
    public void onReceive(ReceiveEvent event) {
        Object object = event.getObject();
        ChannelHandlerContext ctx = event.getCtx();
        if (object instanceof PacketRequestHeartBeat) {
            PacketRequestHeartBeat packet = (PacketRequestHeartBeat) object;
            ctx.channel().writeAndFlush(new PacketResponseHeartBeat(packet.timeStamp));
            cacheServer.getLogger().info("CLIENT | " + packet.memberName + " <> heart-beat (time-to-send=" + (System.currentTimeMillis() - packet.timeStamp) + "ms)");
        } else if (object instanceof PacketRequestRegisterSession) {
            PacketRequestRegisterSession packet = (PacketRequestRegisterSession) object;
            cacheServer.clientManager().addClient(new ClientConnection(packet.memberName, packet.sessionId, ctx));
            cacheServer.getLogger().info("CLIENT | " + packet.memberName + " <> connected (address=" + Utils.wrapInetAddress(ctx) + " sessionId=" + packet.sessionId + ")");
            ctx.channel().writeAndFlush(new PacketResponseRegisterSession(true));
        } else if (object instanceof PacketRequestRegisterBroadcastChannel) {
            PacketRequestRegisterBroadcastChannel packet = (PacketRequestRegisterBroadcastChannel) object;
            cacheServer.clientManager().addBroadcastChannel(packet.memberName, packet.channel);
            cacheServer.getLogger().info("CLIENT | " + packet.memberName + " <> Registered broadcast-channel (channel=" + packet.channel + ")");
        } else if (object instanceof PacketRequestRegisterClientChannel) {
            PacketRequestRegisterClientChannel packet = (PacketRequestRegisterClientChannel) object;
            cacheServer.clientManager().addClientChannel(packet.memberName, packet.channel);
            cacheServer.getLogger().info("CLIENT | " + packet.memberName + " <> Registered client-channel (channel=" + packet.channel + ")");

        } else if (object instanceof PacketBroadcastChannel) {
            PacketBroadcastChannel packet = (PacketBroadcastChannel) object;
            cacheServer.clientManager().getBroadcastReceiver(packet.channel)
                    .stream().filter(conn -> !conn.getMemberName().equalsIgnoreCase(packet.memberName))
                    .forEach(conn -> conn.getConnection().channel().writeAndFlush(new PacketBroadcastToAllClients(packet.memberName, packet.channel, packet.content64)));
        } else if (object instanceof PacketBroadcastRequestClient) {
            PacketBroadcastRequestClient packet = (PacketBroadcastRequestClient) object;
            ClientConnection clientConnection = cacheServer.clientManager().getClientName(packet.clientName);
            if (clientConnection != null) {
                CompletableFuture<String> future = new CompletableFuture<>();
                cacheServer.clientManager().registerCallback(packet.futureId, future);
                clientConnection.getConnection().channel().writeAndFlush(new PacketBroadcastToClient(packet.memberName, packet.futureId, packet.clientChannel, packet.content64));
                try {
                    String callback64 = future.get();
                    ctx.channel().writeAndFlush(new PacketBroadcastContentToClient(packet.clientName, packet.futureId, packet.clientChannel, callback64));
                    cacheServer.clientManager().removeCallback(packet.futureId);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else if (object instanceof PacketBroadcastResponseContent) {
            PacketBroadcastResponseContent packet = (PacketBroadcastResponseContent) object;
            CompletableFuture<String> future = cacheServer.clientManager().getCallback(packet.futureId);
            if (future != null) {
                future.complete(packet.content64);
            }
        } else if (object instanceof PacketRequestCacheSet) {
            PacketRequestCacheSet packet = (PacketRequestCacheSet) object;
            cacheServer.cachePool().set(packet.name, packet.key, packet.base64);
            ctx.channel().writeAndFlush(new PacketResponseCacheSet(packet.futureId, packet.name, packet.key));
        } else if (object instanceof PacketRequestCacheGet) {
            PacketRequestCacheGet packet = (PacketRequestCacheGet) object;
            String content64 = cacheServer.cachePool().get(packet.name, packet.key);
            ctx.channel().writeAndFlush(new PacketResponseCacheGet(packet.id, packet.name, packet.key, content64));

        } else if (object instanceof PacketRequestCacheSetAll) {
            PacketRequestCacheSetAll packet = (PacketRequestCacheSetAll) object;
            cacheServer.cachePool().setAll(packet.name, packet.base64Map);
            ctx.channel().writeAndFlush(new PacketResponseCacheSetAll(packet.futureId, packet.name));

        } else if (object instanceof PacketRequestCacheDelete) {
            PacketRequestCacheDelete packet = (PacketRequestCacheDelete) object;
            cacheServer.cachePool().delete(packet.name, packet.key);
            ctx.channel().writeAndFlush(new PacketResponseCacheDelete(packet.futureId, packet.name, packet.key));

        } else if (object instanceof PacketRequestCacheContains) {
            PacketRequestCacheContains packet = (PacketRequestCacheContains) object;
            boolean contains = cacheServer.cachePool().contains(packet.name, packet.key);
            ctx.channel().writeAndFlush(new PacketResponseCacheContains(packet.id, packet.name, packet.key, contains));

        } else if (object instanceof PacketRequestCacheGetAll) {
            PacketRequestCacheGetAll packet = (PacketRequestCacheGetAll) object;
            HashMap<String, String> cacheCopy = cacheServer.cachePool().getAll(packet.name);
            ctx.channel().writeAndFlush(new PacketResponseCacheGetAll(packet.id, packet.name, cacheCopy));
        }
    }
}
