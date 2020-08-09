package eu.levenproxy.netcache.client;

import com.esotericsoftware.kryonetty.ThreadedClient;
import com.esotericsoftware.kryonetty.kryo.KryoNetty;
import com.esotericsoftware.minlog.Log;
import eu.levenproxy.netcache.client.jobs.HeartbeatJob;
import eu.levenproxy.netcache.config.ClientConfig;
import eu.levenproxy.netcache.config.GsonConfig;
import eu.levenproxy.netcache.utils.Logger;
import eu.levenproxy.netcache.utils.Utils;
import eu.levenproxy.netcache.utils.cron.CronTimerTask;

import java.util.concurrent.Executors;

public class NetCacheClient {

    private final Logger logger;
    private final Thread loggerThread;

    private final String memberName;
    private final String sessionId;

    private final ThreadedClient kryoClient;
    private final NetCacheClientProcessor processor;
    private final NetCacheBroadcastManager netCacheBroadcastManager;

    private final CronTimerTask cronTimerTask;

    private final String host;
    private final int tcpPort;

    public NetCacheClient(String configName, KryoNetty kryoNetty) {
        this.logger = new Logger();
        Log.set(Log.LEVEL_NONE);
        this.loggerThread = new Thread(logger::start);

        ClientConfig clientConfig = GsonConfig.readConfig(Utils.getGson(), configName, ClientConfig.class, new ClientConfig("localhost", 4040, "sample-client"));
        this.host = clientConfig.getServerAddress();
        this.tcpPort = clientConfig.getServerPort();
        this.memberName = clientConfig.getMemberName();
        this.sessionId = Utils.getAlphaNumericString(32);

        this.kryoClient = new ThreadedClient(kryoNetty);
        this.kryoClient.eventHandler().register(new NetCacheClientListener(this));

        this.processor = new NetCacheClientProcessor(this);
        this.netCacheBroadcastManager = new NetCacheBroadcastManager(this);

        this.cronTimerTask = new CronTimerTask(Executors.newCachedThreadPool());
        this.cronTimerTask.registerJob(new HeartbeatJob(this));

        this.processor.getStartFuture().whenComplete((value, throwable) -> {
            if(value) {
                cronTimerTask.start();
                loggerThread.start();
            }
        });
    }

    public NetCacheBroadcastManager broadcastManager() {
        return netCacheBroadcastManager;
    }

    public ThreadedClient kryoClient() {
        return kryoClient;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMemberName() {
        return memberName;
    }

    public CronTimerTask cronTimerTask() {
        return cronTimerTask;
    }

    public void start() {
        try {
            logger.info("Connecting to server " + host + ":" + tcpPort + "...");
            this.kryoClient.connect(this.host, this.tcpPort);
            processor.getStartFuture().join();
        } catch (Exception e) {
            e.printStackTrace();
            processor.getStartFuture().complete(false);
        }
    }

    public boolean isConnected() {
        // TODO: Wait for 0.6.4
        return true;
    }

    public void stop() {
        kryoClient.close();
        cronTimerTask.stop();
        loggerThread.interrupt();
    }

    public NetCacheClientProcessor process() {
        return this.processor;
    }

}
