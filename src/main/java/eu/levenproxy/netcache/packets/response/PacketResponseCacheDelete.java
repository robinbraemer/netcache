package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseCacheDelete implements Serializable {

    public String futureId;
    public String name;
    public String key;

    public PacketResponseCacheDelete() {
    }

    public PacketResponseCacheDelete(String futureId, String name, String key) {
        this.futureId = futureId;
        this.name = name;
        this.key = key;
    }
}
