package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketBroadcastResponseContent implements Serializable {

    public String memberName;
    public String futureId;
    public String content64;

    public PacketBroadcastResponseContent() {
    }

    public PacketBroadcastResponseContent(String memberName, String futureId, String content64) {
        this.memberName = memberName;
        this.futureId = futureId;
        this.content64 = content64;
    }
}
