package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestCacheSet implements Serializable {

    public String futureId;
    public String name;
    public String key;
    public String base64;

    public PacketRequestCacheSet() {
    }

    public PacketRequestCacheSet(String futureId, String name, String key, String base64) {
        this.futureId = futureId;
        this.name = name;
        this.key = key;
        this.base64 = base64;
    }
}
