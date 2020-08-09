package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketBroadcastToClient implements Serializable {

    public String clientName;
    public String futureId;
    public String channel;
    public String content64;

    public PacketBroadcastToClient() {
    }

    public PacketBroadcastToClient(String clientName, String futureId, String channel, String content64) {
        this.clientName = clientName;
        this.futureId = futureId;
        this.channel = channel;
        this.content64 = content64;
    }
}
