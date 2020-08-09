package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketBroadcastToAllClients implements Serializable {

    public String clientName;
    public String channel;
    public String content64;

    public PacketBroadcastToAllClients() {
    }

    public PacketBroadcastToAllClients(String clientName, String channel, String content64) {
        this.clientName = clientName;
        this.channel = channel;
        this.content64 = content64;
    }
}
