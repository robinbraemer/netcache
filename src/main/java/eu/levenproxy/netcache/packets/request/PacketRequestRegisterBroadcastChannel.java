package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestRegisterBroadcastChannel implements Serializable {

    public String memberName;
    public String channel;

    public PacketRequestRegisterBroadcastChannel() {
    }

    public PacketRequestRegisterBroadcastChannel(String memberName, String channel) {
        this.memberName = memberName;
        this.channel = channel;
    }
}
