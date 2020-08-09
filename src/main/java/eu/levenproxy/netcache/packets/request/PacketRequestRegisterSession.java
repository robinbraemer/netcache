package eu.levenproxy.netcache.packets.request;

import java.io.Serializable;

public class PacketRequestRegisterSession implements Serializable {

    public String memberName;
    public String sessionId;

    public PacketRequestRegisterSession() {
    }

    public PacketRequestRegisterSession(String memberName, String sessionId) {
        this.memberName = memberName;
        this.sessionId = sessionId;
    }

}
