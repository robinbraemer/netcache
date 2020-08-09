package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketBroadcastChannel implements Serializable {

    public String memberName;
    public String channel;
    public String content64;

    public PacketBroadcastChannel() {
    }

    public PacketBroadcastChannel(String memberName, String channel, String content64) {
        this.memberName = memberName;
        this.channel = channel;
        this.content64 = content64;
    }
}
