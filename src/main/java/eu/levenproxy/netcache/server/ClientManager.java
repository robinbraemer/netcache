package eu.levenproxy.netcache.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class ClientManager {

    private HashMap<String, ClientConnection> clientMap;
    private HashMap<String, CompletableFuture<String>> clientCallbackMap;

    public ClientManager() {
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

    public void addClient(ClientConnection connection) {
        this.clientMap.put(connection.getMemberName(), connection);
    }

    public ClientConnection getClientName(String memberName) {
        return this.clientMap.getOrDefault(memberName, null);
    }

    public ClientConnection getClientSessionId(String sessionId) {
        return this.clientMap.entrySet().stream().filter(entry -> entry.getValue().getSessionId().equals(sessionId)).findAny().get().getValue();
    }

    public void removeClient(String memberName) {
        this.clientMap.remove(memberName);
    }

    public HashSet<ClientConnection> getBroadcastReceiver(String channel) {
        HashSet<ClientConnection> hashSet = new HashSet<>();
        this.clientMap.forEach((key, value) -> {
            if(value.hasBroadcastChannel(channel)) {
                hashSet.add(value);
            }
        });
        return hashSet;
    }

    public ChannelHandlerContext getClientReceiver(String memberName, String channel) {
        ChannelHandlerContext connection = null;
        for(ClientConnection clientConnection : this.clientMap.values()) {
            if(clientConnection.hasClientChannel(channel)) {
                connection = clientConnection.getConnection();
                break;
            }
        }
        return connection;
    }

    public void addBroadcastChannel(String memberName, String channel) {
        ClientConnection clientConnection = getClientName(memberName);
        if(clientConnection != null) {
            clientConnection.addBroadcastChannel(channel);
            clientMap.put(memberName, clientConnection);
        }
    }

    public void addClientChannel(String memberName, String channel) {
        ClientConnection clientConnection = getClientName(memberName);
        if(clientConnection != null) {
            clientConnection.addClientChannel(channel);
            clientMap.put(memberName, clientConnection);
        }
    }
}
