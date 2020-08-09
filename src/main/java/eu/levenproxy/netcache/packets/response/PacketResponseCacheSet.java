package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseCacheSet implements Serializable {

    public String futureId;
    public String name;
    public String key;

    public PacketResponseCacheSet() {
    }

    public PacketResponseCacheSet(String futureId, String name, String key) {
        this.futureId = futureId;
        this.name = name;
        this.key = key;
    }
}
