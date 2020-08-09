package eu.levenproxy.netcache.storage;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Storage {

    private final StorageCachePool cachePool;
    private final String name;
    private final ConcurrentHashMap<String, String> cache;

    public Storage(StorageCachePool cachePool, String name) {
        this.cachePool = cachePool;
        this.name = name;
        this.cache = new ConcurrentHashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getFromCache(String key) {
        return cache.getOrDefault(key, null);
    }

    public boolean isInCache(String key) {
        return cache.containsKey(key);
    }

    public void setInCache(String key, String content) {
        cache.put(key, content);
    }

    public void setAllInCache(HashMap<String, String> hashMap) {
        cache.putAll(hashMap);
    }

    public HashMap<String, String> getAllFromCache() {
        return new HashMap<>(cache);
    }

    public void deleteFromCache(String key) {
        cache.remove(key);
    }

    public StorageCachePool getCachePool() {
        return cachePool;
    }

    public void load() {
        HashMap<String, String> dataMap = loadContent();
        this.cache.putAll(dataMap);
        dataMap.clear();
    }

    public abstract HashMap<String, String> loadContent();
    public abstract String get(String key);
    public abstract boolean contains(String key);
    public abstract void set(String key, String content);
    public abstract void setAll(HashMap<String, String> hashMap);
    public abstract HashMap<String, String> getAll();
    public abstract void delete(String key);

}
