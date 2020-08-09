package eu.levenproxy.netcache.storage;

import eu.levenproxy.netcache.server.CacheServer;
import eu.levenproxy.netcache.utils.SQLCredentials;
import eu.levenproxy.netcache.utils.benchmark.Benchmark;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageCachePool {

    private final CacheServer cacheServer;
    private final SQLDriver sqlDriver;
    private final ConcurrentHashMap<String, Storage> cache;
    private final ExecutorService executor;

    public StorageCachePool(CacheServer cacheServer, SQLCredentials sqlCredentials) {
        this.cacheServer = cacheServer;
        this.sqlDriver = new SQLDriver(sqlCredentials);
        this.cache = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
        cacheServer.getLogger().info("Start loading storages...");
        this.sqlDriver.getTableNames().forEach(this::getStorage);
        cacheServer.getLogger().info("Loaded all storages!");
    }

    public SQLDriver getSqlDriver() {
        return this.sqlDriver;
    }

    private Storage getStorage(String name) {
        Storage storage;
        if (this.cache.containsKey(name)) {
            storage = this.cache.get(name);
        } else {
            Benchmark benchmark = new Benchmark().record();
            cacheServer.getLogger().info("Loading storage '" + name + "'...");
            storage = new SQLStorage(sqlDriver, this, name);
            storage.load();
            this.cache.put(name, storage);
            cacheServer.getLogger().info("Successful loaded '" + name + "' in " + benchmark.stop().asMillis() + "!");
        }
        return storage;
    }

    public void run(Runnable runnable) {
        this.executor.execute(runnable);
    }

    public boolean contains(String name, String key) {
        try {
            return getStorage(name).contains(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String get(String name, String key) {
        try {
            return getStorage(name).get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> getAll(String name) {
        try {
            return getStorage(name).getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(String name, String key, String content) {
            Storage storage = getStorage(name);
            storage.set(key, content);
            cache.put(name, storage);
    }

    public void setAll(String name, HashMap<String, String> hashMap) {
            Storage storage = getStorage(name);
            storage.setAll(hashMap);
            cache.put(name, storage);
    }

    public void delete(String name, String key) {
            Storage storage = getStorage(name);
            storage.delete(key);
            cache.put(name, storage);
    }

}
