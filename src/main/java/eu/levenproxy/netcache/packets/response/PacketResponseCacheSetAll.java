package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseCacheSetAll implements Serializable {

    public String futureId;
    public String name;

    public PacketResponseCacheSetAll() {
    }

    public PacketResponseCacheSetAll(String futureId, String name) {
        this.futureId = futureId;
        this.name = name;
    }
}
