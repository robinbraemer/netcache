package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestCacheGetAll implements Serializable {

    public String id;
    public String name;

    public PacketRequestCacheGetAll() {
    }

    public PacketRequestCacheGetAll(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
