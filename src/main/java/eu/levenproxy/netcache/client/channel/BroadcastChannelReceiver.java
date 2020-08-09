package eu.levenproxy.netcache.client.channel;

public interface BroadcastChannelReceiver {

    String getName();
    void onReceive(String clientName, Object object);
}
