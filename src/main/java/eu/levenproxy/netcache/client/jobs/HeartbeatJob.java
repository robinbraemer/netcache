package eu.levenproxy.netcache.client.jobs;

import eu.levenproxy.netcache.client.CacheClient;
import eu.levenproxy.netcache.packets.request.PacketRequestHeartBeat;
import eu.levenproxy.netcache.utils.cron.CronJob;

import java.lang.management.ManagementFactory;

public class HeartbeatJob extends CronJob {

    private final CacheClient cacheClient;

    public HeartbeatJob(CacheClient cacheClient) {
        super("HeartbeatJob", 60, true);
        this.cacheClient = cacheClient;
    }

    @Override
    public void onTickRateFired() {
        PacketRequestHeartBeat packetRequestHeartBeat = packet(cacheClient.getMemberName(), cacheClient.getSessionId());
        cacheClient.kryoClient().send(packetRequestHeartBeat);
    }

    public PacketRequestHeartBeat packet(String memberName, String sessionId) {
        long timeStamp = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long usedRam = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        long freeRam = (runtime.maxMemory() / 1048576L - (runtime.totalMemory() - runtime.freeMemory()) / 1048576L);
        long maxRam = runtime.maxMemory() / 1048576L;
        int threads = ManagementFactory.getThreadMXBean().getThreadCount();
        int classes = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
        double cpuLoad = os.getSystemCpuLoad();
        return new PacketRequestHeartBeat(sessionId, memberName, timeStamp);
    }
}
