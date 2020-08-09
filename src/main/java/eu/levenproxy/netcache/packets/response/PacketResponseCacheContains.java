package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseCacheContains implements Serializable {

    public String id;
    public String name;
    public String key;
    public boolean value;

    public PacketResponseCacheContains() {
    }

    public PacketResponseCacheContains(String id, String name, String key, boolean value) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.value = value;
    }
}
