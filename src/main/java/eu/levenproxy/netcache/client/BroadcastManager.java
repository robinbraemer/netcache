package eu.levenproxy.netcache.client;

import eu.levenproxy.netcache.client.channel.BroadcastChannelReceiver;
import eu.levenproxy.netcache.client.channel.ClientChannelReceiver;
import eu.levenproxy.netcache.packets.request.PacketRequestRegisterBroadcastChannel;
import eu.levenproxy.netcache.packets.request.PacketRequestRegisterClientChannel;

import java.util.HashMap;

public class BroadcastManager {

    private CacheClient cacheClient;
    private HashMap<String, BroadcastChannelReceiver> receiverMap;
    private HashMap<String, ClientChannelReceiver> clientReceiverMap;

    public BroadcastManager(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
        this.receiverMap = new HashMap<>();
        this.clientReceiverMap = new HashMap<>();
    }

    public void registerClientReceiver(ClientChannelReceiver clientChannelReceiver) {
        clientReceiverMap.put(clientChannelReceiver.getName(), clientChannelReceiver);
        cacheClient.kryoClient().send(new PacketRequestRegisterClientChannel(cacheClient.getMemberName(), clientChannelReceiver.getName()));
    }

    public boolean hasClientReceiver(String channel) {
        return clientReceiverMap.containsKey(channel);
    }

    public ClientChannelReceiver getClientReceiver(String channel) {
        return clientReceiverMap.getOrDefault(channel, null);
    }

    public void registerBroadcastReceiver(BroadcastChannelReceiver broadcastChannelReceiver) {
        receiverMap.put(broadcastChannelReceiver.getName(), broadcastChannelReceiver);
        cacheClient.kryoClient().send(new PacketRequestRegisterBroadcastChannel(cacheClient.getMemberName(), broadcastChannelReceiver.getName()));
    }

    public boolean hasReceiver(String channel) {
        return receiverMap.containsKey(channel);
    }

    public BroadcastChannelReceiver getReceiver(String channel) {
        return receiverMap.getOrDefault(channel, null);
    }


}
