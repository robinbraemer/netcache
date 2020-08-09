package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestRegisterClientChannel implements Serializable {

    public String memberName;
    public String channel;

    public PacketRequestRegisterClientChannel() {
    }

    public PacketRequestRegisterClientChannel(String memberName, String channel) {
        this.memberName = memberName;
        this.channel = channel;
    }
}
