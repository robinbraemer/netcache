package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestHeartBeat implements Serializable {

    public String sessionId;
    public String memberName;
    public long timeStamp;

    public PacketRequestHeartBeat() {
    }

    public PacketRequestHeartBeat(String sessionId, String memberName, long timeStamp) {
        this.sessionId = sessionId;
        this.memberName = memberName;
        this.timeStamp = timeStamp;
    }
}
