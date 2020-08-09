package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseHeartBeat implements Serializable {

    public long timeStamp;

    public PacketResponseHeartBeat() {
    }

    public PacketResponseHeartBeat(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
