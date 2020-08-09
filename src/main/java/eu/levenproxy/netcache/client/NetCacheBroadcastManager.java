package eu.levenproxy.netcache.client;

import eu.levenproxy.netcache.client.channel.BroadcastChannelReceiver;
import eu.levenproxy.netcache.client.channel.ClientChannelReceiver;
import eu.levenproxy.netcache.packets.request.PacketRequestRegisterBroadcastChannel;
import eu.levenproxy.netcache.packets.request.PacketRequestRegisterClientChannel;

import java.util.HashMap;

public class NetCacheBroadcastManager {

    private NetCacheClient netCacheClient;
    private HashMap<String, BroadcastChannelReceiver> receiverMap;
    private HashMap<String, ClientChannelReceiver> clientReceiverMap;

    public NetCacheBroadcastManager(NetCacheClient netCacheClient) {
        this.netCacheClient = netCacheClient;
        this.receiverMap = new HashMap<>();
        this.clientReceiverMap = new HashMap<>();
    }

    public void registerClientReceiver(ClientChannelReceiver clientChannelReceiver) {
        clientReceiverMap.put(clientChannelReceiver.getName(), clientChannelReceiver);
        netCacheClient.kryoClient().send(new PacketRequestRegisterClientChannel(netCacheClient.getMemberName(), clientChannelReceiver.getName()));
    }

    public boolean hasClientReceiver(String channel) {
        return clientReceiverMap.containsKey(channel);
    }

    public ClientChannelReceiver getClientReceiver(String channel) {
        return clientReceiverMap.getOrDefault(channel, null);
    }

    public void registerBroadcastReceiver(BroadcastChannelReceiver broadcastChannelReceiver) {
        receiverMap.put(broadcastChannelReceiver.getName(), broadcastChannelReceiver);
        netCacheClient.kryoClient().send(new PacketRequestRegisterBroadcastChannel(netCacheClient.getMemberName(), broadcastChannelReceiver.getName()));
    }

    public boolean hasReceiver(String channel) {
        return receiverMap.containsKey(channel);
    }

    public BroadcastChannelReceiver getReceiver(String channel) {
        return receiverMap.getOrDefault(channel, null);
    }


}
