package eu.levenproxy.netcache.packets.response;

import java.io.Serializable;

public class PacketResponseRegisterSession implements Serializable {

    public boolean success;

    public PacketResponseRegisterSession() {
    }

    public PacketResponseRegisterSession(boolean success) {
        this.success = success;
    }
}
