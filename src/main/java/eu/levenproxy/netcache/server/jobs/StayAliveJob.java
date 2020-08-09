package eu.levenproxy.netcache.server.jobs;


import eu.levenproxy.netcache.server.CacheServer;
import eu.levenproxy.netcache.utils.cron.CronJob;

public class StayAliveJob extends CronJob {

    final CacheServer cacheServer;

    public StayAliveJob(CacheServer cacheServer) {
        super("StayAliveJob", 45, true);
        this.cacheServer = cacheServer;
    }

    @Override
    public void onTickRateFired() {
        cacheServer.cachePool().getSqlDriver().executeQuery("SELECT 1");
    }
}
