package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;
import java.util.HashMap;

public class PacketRequestCacheSetAll implements Serializable {

    public String futureId;
    public String name;
    public HashMap<String, String> base64Map;

    public PacketRequestCacheSetAll() {
    }

    public PacketRequestCacheSetAll(String futureId, String name, HashMap<String, String> base64Map) {
        this.futureId = futureId;
        this.name = name;
        this.base64Map = base64Map;
    }
}
