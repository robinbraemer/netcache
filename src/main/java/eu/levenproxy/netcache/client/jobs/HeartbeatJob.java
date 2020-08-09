package eu.levenproxy.netcache.client.jobs;

import eu.levenproxy.netcache.client.NetCacheClient;
import eu.levenproxy.netcache.packets.request.PacketRequestHeartBeat;
import eu.levenproxy.netcache.utils.cron.CronJob;

public class HeartbeatJob extends CronJob {

    private final NetCacheClient netCacheClient;

    public HeartbeatJob(NetCacheClient netCacheClient) {
        super("HeartbeatJob", 60, true);
        this.netCacheClient = netCacheClient;
    }

    @Override
    public void onTickRateFired() {
        netCacheClient.kryoClient().send(new PacketRequestHeartBeat(netCacheClient.getSessionId(), netCacheClient.getMemberName(), System.currentTimeMillis()));
    }

}
