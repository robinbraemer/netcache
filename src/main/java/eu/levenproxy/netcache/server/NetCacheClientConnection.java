package eu.levenproxy.netcache.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;

public class NetCacheClientConnection {

    private final String memberName;
    private final String sessionId;
    private final HashSet<String> broadcastChannel;
    private final HashSet<String> clientChannel;
    private final ChannelHandlerContext connection;

    public NetCacheClientConnection(String memberName, String sessionId, ChannelHandlerContext connection) {
        this.memberName = memberName;
        this.sessionId = sessionId;
        this.broadcastChannel = new HashSet<>();
        this.clientChannel = new HashSet<>();
        this.connection = connection;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ChannelHandlerContext getConnection() {
        return connection;
    }

    public void addBroadcastChannel(String channel) {
        this.broadcastChannel.add(channel);
    }

    public void addClientChannel(String channel) {
        this.clientChannel.add(channel);
    }

    public boolean hasBroadcastChannel(String channel) {
        return this.broadcastChannel.contains(channel);
    }

    public boolean hasClientChannel(String channel) {
        return this.broadcastChannel.contains(channel);
    }
}
