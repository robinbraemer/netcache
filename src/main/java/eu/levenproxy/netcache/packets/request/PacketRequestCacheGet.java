package eu.levenproxy.netcache.packets.request;


import java.io.Serializable;

public class PacketRequestCacheGet implements Serializable {

    public String id;
    public String name;
    public String key;

    public PacketRequestCacheGet() {
    }

    public PacketRequestCacheGet(String id, String name, String key) {
        this.id = id;
        this.name = name;
        this.key = key;
    }

}
