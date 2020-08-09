package eu.levenproxy.netcache.utils;

import com.esotericsoftware.kryonetty.kryo.KryoNetty;
import eu.levenproxy.netcache.packets.request.*;
import eu.levenproxy.netcache.packets.response.*;

public class KryoContext {

    private static final KryoNetty KRYO_NETTY = new KryoNetty()
            .register(
                    // Session-Packets
                    PacketRequestRegisterSession.class,
                    PacketResponseRegisterSession.class,

                    // Broadcast-Packets
                    PacketRequestRegisterBroadcastChannel.class,
                    PacketRequestRegisterClientChannel.class,

                    PacketBroadcastChannel.class,
                    PacketBroadcastRequestClient.class,
                    PacketBroadcastResponseContent.class,

                    PacketBroadcastToClient.class,
                    PacketBroadcastContentToClient.class,
                    PacketBroadcastToAllClients.class,

                    // Cache-Packets
                    PacketRequestCacheContains.class,
                    PacketRequestCacheDelete.class,
                    PacketRequestCacheGet.class,
                    PacketRequestCacheGetAll.class,
                    PacketRequestCacheSet.class,
                    PacketRequestCacheSetAll.class,

                    PacketResponseCacheContains.class,
                    PacketResponseCacheDelete.class,
                    PacketResponseCacheGet.class,
                    PacketResponseCacheGetAll.class,
                    PacketResponseCacheSet.class,
                    PacketResponseCacheSetAll.class,

                    // Heartbeat-Packets
                    PacketRequestHeartBeat.class,
                    PacketResponseHeartBeat.class
            );
    ;


    public static KryoNetty getDefaultKryoNetty() {
        return KRYO_NETTY;
    }

}
