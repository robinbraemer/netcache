package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketBroadcastContentToClient implements Serializable {

    public String memberName;
    public String futureId;
    public String channel;
    public String content64;

    public PacketBroadcastContentToClient() {
    }

    public PacketBroadcastContentToClient(String memberName, String futureId, String channel, String content64) {
        this.memberName = memberName;
        this.futureId = futureId;
        this.channel = channel;
        this.content64 = content64;
    }
}
