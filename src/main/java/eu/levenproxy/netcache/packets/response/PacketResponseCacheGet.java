package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseCacheGet implements Serializable {

    public String id;
    public String name;
    public String key;
    public String base64;

    public PacketResponseCacheGet() {
    }

    public PacketResponseCacheGet(String id, String name, String key, String base64) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.base64 = base64;
    }
}
