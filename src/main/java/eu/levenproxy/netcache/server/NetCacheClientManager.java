package eu.levenproxy.netcache.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class NetCacheClientManager {

    private HashMap<String, NetCacheClientConnection> clientMap;
    private HashMap<String, CompletableFuture<String>> clientCallbackMap;

    public NetCacheClientManager() {
        this.clientMap = new HashMap<>();
        this.clientCallbackMap = new HashMap<>();
    }


    public void registerCallback(String futureId, CompletableFuture<String> future) {
        this.clientCallbackMap.put(futureId, future);
    }

    public CompletableFuture<String> getCallback(String futureId) {
        return this.clientCallbackMap.getOrDefault(futureId, null);
    }

    public void removeCallback(String futureId) {
        this.clientCallbackMap.remove(futureId);
    }

    public void addClient(NetCacheClientConnection connection) {
        this.clientMap.put(connection.getMemberName(), connection);
    }

    public NetCacheClientConnection getClientName(String memberName) {
        return this.clientMap.getOrDefault(memberName, null);
    }

    public NetCacheClientConnection getClientSessionId(String sessionId) {
        return this.clientMap.entrySet().stream().filter(entry -> entry.getValue().getSessionId().equals(sessionId)).findAny().get().getValue();
    }

    public void removeClient(String memberName) {
        this.clientMap.remove(memberName);
    }

    public HashSet<NetCacheClientConnection> getBroadcastReceiver(String channel) {
        HashSet<NetCacheClientConnection> hashSet = new HashSet<>();
        this.clientMap.forEach((key, value) -> {
            if(value.hasBroadcastChannel(channel)) {
                hashSet.add(value);
            }
        });
        return hashSet;
    }

    public ChannelHandlerContext getClientReceiver(String memberName, String channel) {
        ChannelHandlerContext connection = null;
        for(NetCacheClientConnection netCacheClientConnection : this.clientMap.values()) {
            if(netCacheClientConnection.hasClientChannel(channel)) {
                connection = netCacheClientConnection.getConnection();
                break;
            }
        }
        return connection;
    }

    public void addBroadcastChannel(String memberName, String channel) {
        NetCacheClientConnection netCacheClientConnection = getClientName(memberName);
        if(netCacheClientConnection != null) {
            netCacheClientConnection.addBroadcastChannel(channel);
            clientMap.put(memberName, netCacheClientConnection);
        }
    }

    public void addClientChannel(String memberName, String channel) {
        NetCacheClientConnection netCacheClientConnection = getClientName(memberName);
        if(netCacheClientConnection != null) {
            netCacheClientConnection.addClientChannel(channel);
            clientMap.put(memberName, netCacheClientConnection);
        }
    }
}
