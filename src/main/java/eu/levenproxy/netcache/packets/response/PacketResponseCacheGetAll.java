package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;
import java.util.HashMap;

public class PacketResponseCacheGetAll implements Serializable {

    public String id;
    public String name;
    public HashMap<String, String> content64;

    public PacketResponseCacheGetAll() {
    }

    public PacketResponseCacheGetAll(String id, String name, HashMap<String, String> content64) {
        this.id = id;
        this.name = name;
        this.content64 = content64;
    }
}
