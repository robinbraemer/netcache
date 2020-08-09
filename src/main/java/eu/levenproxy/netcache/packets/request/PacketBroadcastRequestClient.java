package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketBroadcastRequestClient implements Serializable {

    public String memberName;
    public String futureId;
    public String clientName;
    public String clientChannel;
    public String content64;

    public PacketBroadcastRequestClient() {
    }

    public PacketBroadcastRequestClient(String memberName, String futureId, String clientName, String clientChannel, String content64) {
        this.memberName = memberName;
        this.futureId = futureId;
        this.clientName = clientName;
        this.clientChannel = clientChannel;
        this.content64 = content64;
    }
}
