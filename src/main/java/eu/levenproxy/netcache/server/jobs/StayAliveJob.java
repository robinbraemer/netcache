package eu.levenproxy.netcache.server.jobs;


import eu.levenproxy.netcache.server.NetCacheServer;
import eu.levenproxy.netcache.utils.cron.CronJob;

public class StayAliveJob extends CronJob {

    final NetCacheServer netCacheServer;

    public StayAliveJob(NetCacheServer netCacheServer) {
        super("StayAliveJob", 45, true);
        this.netCacheServer = netCacheServer;
    }

    @Override
    public void onTickRateFired() {
        netCacheServer.cachePool().getSqlDriver().executeQuery("SELECT 1");
    }
}
