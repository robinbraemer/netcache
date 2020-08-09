package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestCacheDelete implements Serializable {

    public String futureId;
    public String name;
    public String key;

    public PacketRequestCacheDelete() {
    }

    public PacketRequestCacheDelete(String futureId, String name, String key) {
        this.futureId = futureId;
        this.name = name;
        this.key = key;
    }
}
