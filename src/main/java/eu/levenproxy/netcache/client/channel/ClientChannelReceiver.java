package eu.levenproxy.netcache.client.channel;

public interface ClientChannelReceiver {

    String getName();
    Object onReceive(String memberName, Object object);
}
