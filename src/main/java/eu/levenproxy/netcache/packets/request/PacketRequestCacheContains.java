package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestCacheContains implements Serializable {

    public String id;
    public String name;
    public String key;

    public PacketRequestCacheContains() {
    }

    public PacketRequestCacheContains(String id, String name, String key) {
        this.id = id;
        this.name = name;
        this.key = key;
    }
}
