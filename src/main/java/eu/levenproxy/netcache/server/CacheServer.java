package eu.levenproxy.netcache.server;


import com.esotericsoftware.kryonetty.ThreadedServer;
import com.esotericsoftware.kryonetty.kryo.KryoNetty;
import com.esotericsoftware.minlog.Log;

import eu.levenproxy.netcache.config.ServerConfig;
import eu.levenproxy.netcache.server.jobs.StayAliveJob;
import eu.levenproxy.netcache.storage.StorageCachePool;
import eu.levenproxy.netcache.config.GsonConfig;
import eu.levenproxy.netcache.utils.Logger;
import eu.levenproxy.netcache.utils.Utils;
import eu.levenproxy.netcache.utils.cron.CronTimerTask;

import java.util.concurrent.Executors;

public class CacheServer {

    private final Logger logger;
    private final Thread loggerThread;
    private final ThreadedServer kryoServer;
    private final StorageCachePool cachePool;
    private final CronTimerTask cronTimerTask;
    private final int tcpPort;
    private final ClientManager clientManager;

    public CacheServer(KryoNetty kryoNetty) {
        this.logger = new Logger();
        Log.set(Log.LEVEL_NONE);
        this.loggerThread = new Thread(logger::start);

        ServerConfig config = GsonConfig.readConfig(Utils.getGson(), "server_config.json", ServerConfig.class, new ServerConfig(4040));
        this.tcpPort = config.getServerPort();

        this.kryoServer = new ThreadedServer(kryoNetty);
        this.kryoServer.eventHandler().register(new ServerListener(this));

        this.cachePool = new StorageCachePool(this);
        this.clientManager = new ClientManager();

        this.cronTimerTask = new CronTimerTask(Executors.newCachedThreadPool());
        this.cronTimerTask.registerJob(new StayAliveJob(this));
    }



    public StorageCachePool cachePool() {
        return cachePool;
    }

    public Logger getLogger() {
        return logger;
    }

    public ClientManager clientManager() {
        return clientManager;
    }

    public void start() {
        kryoServer.start(this.tcpPort);
        logger.info("Started server at 0.0.0.0:" + tcpPort);
        cronTimerTask.start();
        loggerThread.start();
    }

    public void stop() {
        kryoServer.close();
        cronTimerTask.stop();
        loggerThread.interrupt();
    }
}
