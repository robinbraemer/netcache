package eu.levenproxy.netcache.client;

import eu.levenproxy.netcache.client.channel.BroadcastChannelReceiver;
import eu.levenproxy.netcache.client.channel.ClientChannelReceiver;
import eu.levenproxy.netcache.packets.request.*;
import eu.levenproxy.netcache.packets.response.*;
import eu.levenproxy.netcache.storage.StorageCodec;
import eu.levenproxy.netcache.utils.Utils;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetCacheClientProcessor {

    private final ExecutorService executor;
    private final CompletableFuture<Boolean> startFuture;
    private final ConcurrentHashMap<String, CompletableFuture<?>> cacheCallbackMap;
    private final ConcurrentHashMap<String, CompletableFuture<?>> broadcastCallbackMap;
    private final NetCacheClient netCacheClient;
    private final StorageCodec storageCodec;

    public NetCacheClientProcessor(NetCacheClient netCacheClient) {
        this.executor = Executors.newCachedThreadPool();
        this.startFuture = new CompletableFuture<>();
        this.cacheCallbackMap = new ConcurrentHashMap<>();
        this.broadcastCallbackMap = new ConcurrentHashMap<>();
        this.storageCodec = new StorageCodec(netCacheClient);
        this.netCacheClient = netCacheClient;
    }

    protected CompletableFuture<Boolean> getStartFuture() {
        return startFuture;
    }

    protected void receivedObject(ChannelHandlerContext ctx, Object object) {
        if (object instanceof PacketResponseRegisterSession) {
            PacketResponseRegisterSession packet = (PacketResponseRegisterSession) object;
            startFuture.complete(packet.success);
            if (packet.success) {
                netCacheClient.getLogger().info("Session successful registered. (memberName=" + netCacheClient.getMemberName() + " sessionId=" + netCacheClient.getSessionId() + ")");
            } else {
                netCacheClient.getLogger().info("Session registration failed. (memberName=" + netCacheClient.getMemberName() + " sessionId=" + netCacheClient.getSessionId() + ")");
                System.exit(-1);
            }
        } else if (object instanceof PacketBroadcastToAllClients) {
            PacketBroadcastToAllClients packet = (PacketBroadcastToAllClients) object;
            if (netCacheClient.broadcastManager().hasReceiver(packet.channel)) {
                BroadcastChannelReceiver receiver = netCacheClient.broadcastManager().getReceiver(packet.channel);
                Object broadcasted = storageCodec.decodeFromBase64(packet.content64);
                executor.execute(() -> receiver.onReceive(packet.clientName, broadcasted));
            }
        } else if (object instanceof PacketBroadcastToClient) {
            PacketBroadcastToClient packet = (PacketBroadcastToClient) object;
            if (netCacheClient.broadcastManager().hasClientReceiver(packet.channel)) {
                ClientChannelReceiver receiver = netCacheClient.broadcastManager().getClientReceiver(packet.channel);
                Object message = storageCodec.decodeFromBase64(packet.content64);
                try {
                    Object response = executor.submit(() -> receiver.onReceive(packet.clientName, message)).get();
                    if (response != null) {
                        String content64 = storageCodec.encodeToBase64(response);
                        ctx.channel().writeAndFlush(new PacketBroadcastResponseContent(packet.clientName, packet.futureId, content64));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (object instanceof PacketBroadcastContentToClient) {
            PacketBroadcastContentToClient packet = (PacketBroadcastContentToClient) object;
            Object responseObject = storageCodec.decodeFromBase64(packet.content64);
            if (broadcastCallbackMap.containsKey(packet.futureId)) {
                ((CompletableFuture<Object>) broadcastCallbackMap.get(packet.futureId)).complete(responseObject);
                broadcastCallbackMap.remove(packet.futureId);
            }
        } else if (object instanceof PacketResponseCacheSet) {
            PacketResponseCacheSet packet = (PacketResponseCacheSet) object;
            if (cacheCallbackMap.containsKey(packet.futureId)) {
                ((CompletableFuture<Boolean>) cacheCallbackMap.get(packet.futureId)).complete(true);
                cacheCallbackMap.remove(packet.futureId);
            }
        } else if (object instanceof PacketResponseCacheSetAll) {
            PacketResponseCacheSetAll packet = (PacketResponseCacheSetAll) object;
            if (cacheCallbackMap.containsKey(packet.futureId)) {
                ((CompletableFuture<Boolean>) cacheCallbackMap.get(packet.futureId)).complete(true);
                cacheCallbackMap.remove(packet.futureId);
            }
        } else if (object instanceof PacketResponseCacheDelete) {
            PacketResponseCacheDelete packet = (PacketResponseCacheDelete) object;
            if (cacheCallbackMap.containsKey(packet.futureId)) {
                ((CompletableFuture<Boolean>) cacheCallbackMap.get(packet.futureId)).complete(true);
                cacheCallbackMap.remove(packet.futureId);
            }

        } else if (object instanceof PacketResponseCacheGet) {
            PacketResponseCacheGet packet = (PacketResponseCacheGet) object;
            Object decodedObject = storageCodec.decodeFromBase64(packet.base64);
            if (cacheCallbackMap.containsKey(packet.id)) {
                ((CompletableFuture<Object>) cacheCallbackMap.get(packet.id)).complete(decodedObject);
                cacheCallbackMap.remove(packet.id);
            }
        } else if (object instanceof PacketResponseCacheContains) {
            PacketResponseCacheContains packet = (PacketResponseCacheContains) object;
            if (cacheCallbackMap.containsKey(packet.id)) {
                ((CompletableFuture<Boolean>) cacheCallbackMap.get(packet.id)).complete(packet.value);
                cacheCallbackMap.remove(packet.id);
            }
        } else if (object instanceof PacketResponseCacheGetAll) {
            PacketResponseCacheGetAll packet = (PacketResponseCacheGetAll) object;
            if (cacheCallbackMap.containsKey(packet.id)) {
                HashMap<String, String> cacheCopy = new HashMap<>(packet.content64);
                ((CompletableFuture<HashMap<String, ?>>) cacheCallbackMap.get(packet.id)).complete(storageCodec.decodeFromBase64Map(cacheCopy));
                cacheCallbackMap.remove(packet.id);
            }
        }
    }


    public void registerBroadcastReceiver(BroadcastChannelReceiver broadcastChannelReceiver) {
        netCacheClient.getLogger().info("Registering broadcast-channel-receiver " + broadcastChannelReceiver.getName());
        netCacheClient.broadcastManager().registerBroadcastReceiver(broadcastChannelReceiver);
        netCacheClient.kryoClient().send(new PacketRequestRegisterBroadcastChannel(netCacheClient.getMemberName(), broadcastChannelReceiver.getName()));
    }

    public void registerClientReceiver(ClientChannelReceiver clientChannelReceiver) {
        netCacheClient.getLogger().info("Registering client-channel-receiver " + clientChannelReceiver.getName());
        netCacheClient.broadcastManager().registerClientReceiver(clientChannelReceiver);
        netCacheClient.kryoClient().send(new PacketRequestRegisterClientChannel(netCacheClient.getMemberName(), clientChannelReceiver.getName()));
    }


    public <T> void broadcast(String channel, T object) {
        if (!(object instanceof Serializable))
            throw new IllegalArgumentException("object isn't instanceof 'Serializable.class'! Please implement it for serialization.");
        if (netCacheClient.getSessionId() == null)
            throw new NullPointerException("client isn't registered with server or couldn't get the sessionId!");
        String base64 = storageCodec.encodeToBase64(object);
        this.netCacheClient.kryoClient().send(new PacketBroadcastChannel(netCacheClient.getMemberName(), channel, base64), true);
    }

    public <T> void broadcastAsync(String channel, T object) {
        this.executor.submit(() -> {
            if (!(object instanceof Serializable))
                throw new IllegalArgumentException("object isn't instanceof 'Serializable.class'! Please implement it for serialization.");
            if (netCacheClient.getSessionId() == null)
                throw new NullPointerException("client isn't registered with server or couldn't get the sessionId!");
            String base64 = storageCodec.encodeToBase64(object);
            this.netCacheClient.kryoClient().send(new PacketBroadcastChannel(netCacheClient.getMemberName(), channel, base64), false);
        });
    }

    public <T> T broadcastClient(String clientName, String channel, Object object) {
        try {
            if (object instanceof Map)
                throw new IllegalArgumentException("object is instanceof Map. Please use #broadcastOnChannelMap() instead!");
            if (!(object instanceof Serializable))
                throw new IllegalArgumentException("object isn't instanceof 'Serializable.class'! Please implement it for serialization.");
            if (netCacheClient.getSessionId() == null)
                throw new NullPointerException("client isn't registered with server or couldn't get the sessionId!");
            String futureId = Utils.getAlphaNumericString(128);
            String base64 = storageCodec.encodeToBase64(object);
            CompletableFuture<T> future = new CompletableFuture<>();
            this.broadcastCallbackMap.put(futureId, future);
            this.netCacheClient.kryoClient().send(new PacketBroadcastRequestClient(netCacheClient.getMemberName(), futureId, clientName, channel, base64));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /* Cache-Methods */


    /**
     * This method is used to set a value from
     * the cache-server.
     *
     * @param name   The primary storage name
     * @param key    The unique key of the requested value
     * @param object The object you want to store
     */
    public boolean set(String name, String key, Serializable object) {
        String futureId = Utils.getAlphaNumericString(128);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            cacheCallbackMap.put(futureId, future);
            String base64 = storageCodec.encodeToBase64(object);
            netCacheClient.kryoClient().send(new PacketRequestCacheSet(futureId, name, key, base64), true);
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setAsync(String name, String key, Serializable object) {
        try {
            return executor.submit(() -> {
                String futureId = Utils.getAlphaNumericString(128);
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                try {
                    cacheCallbackMap.put(futureId, future);
                    String base64 = storageCodec.encodeToBase64(object);
                    netCacheClient.kryoClient().send(new PacketRequestCacheSet(futureId, name, key, base64), false);
                    return future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * This method is used to set all values in
     * the cache-server.
     *
     * @param name       The primary storage name
     * @param contentMap The hashMap which you want to set
     */
    public boolean setAll(String name, HashMap<String, ?> contentMap) {
        String futureId = Utils.getAlphaNumericString(128);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            cacheCallbackMap.put(futureId, future);
            HashMap<String, String> base64Map = storageCodec.encodeToBase64Map(contentMap);
            netCacheClient.kryoClient().send(new PacketRequestCacheSetAll(futureId, name, base64Map), true);
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setAllAsync(String name, HashMap<String, ?> contentMap) {
        try {
            return executor.submit(() -> {
                String futureId = Utils.getAlphaNumericString(128);
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                try {
                    cacheCallbackMap.put(futureId, future);
                    HashMap<String, String> base64Map = storageCodec.encodeToBase64Map(contentMap);
                    netCacheClient.kryoClient().send(new PacketRequestCacheSetAll(futureId, name, base64Map), false);
                    return future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * This method is used to delete a value from
     * the cache-server.
     *
     * @param name The primary storage name
     * @param key  The unique key of the requested value
     */
    public boolean delete(String name, String key) {
        String futureId = Utils.getAlphaNumericString(128);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            cacheCallbackMap.put(futureId, future);
            netCacheClient.kryoClient().send(new PacketRequestCacheDelete(futureId, name, key), false);
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAsync(String name, String key) {
        try {
            return executor.submit(() -> {
                String futureId = Utils.getAlphaNumericString(128);
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                try {
                    cacheCallbackMap.put(futureId, future);
                    netCacheClient.kryoClient().send(new PacketRequestCacheDelete(futureId, name, key), false);
                    return future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * This method is used to get a value as object from
     * the cache-server. The response is send and processed async.
     *
     * @param name The primary storage name
     * @param key  The unique key of the requested value
     * @return A CompletableFuture with the requested object as value
     */
    public <T> T get(String name, String key, Class<T> clazz) {
        String futureId = Utils.getAlphaNumericString(128);
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        try {
            cacheCallbackMap.put(futureId, completableFuture);
            netCacheClient.kryoClient().send(new PacketRequestCacheGet(futureId, name, key), true);
            return completableFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getAsync(String name, String key, Class<T> clazz) {
        try {
            return executor.submit(() -> {
                String futureId = Utils.getAlphaNumericString(128);
                CompletableFuture<T> completableFuture = new CompletableFuture<>();
                try {
                    cacheCallbackMap.put(futureId, completableFuture);
                    netCacheClient.kryoClient().send(new PacketRequestCacheGet(futureId, name, key), true);
                    return completableFuture.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This method is used to check if a value is set in
     * the cache-server. The response is send and processed async.
     *
     * @param name The primary storage name
     * @param key  The unique key of the requested value
     * @return A CompletableFuture with the boolean value
     */
    public boolean contains(String name, String key) {
        String futureId = Utils.getAlphaNumericString(128);
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        try {
            cacheCallbackMap.put(futureId, completableFuture);
            netCacheClient.kryoClient().send(new PacketRequestCacheContains(futureId, name, key), true);
            return completableFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean containsAsync(String name, String key) {
        try {
            return executor.submit(() -> {
                String futureId = Utils.getAlphaNumericString(128);
                CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
                try {
                    cacheCallbackMap.put(futureId, completableFuture);
                    netCacheClient.kryoClient().send(new PacketRequestCacheContains(futureId, name, key), false);
                    return completableFuture.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * This method is used to get all values as object from
     * the cache-server. The response is send and processed async.
     *
     * @param name The primary storage name
     * @return A CompletableFuture with the requested cache as hashmap
     */
    public <T> HashMap<String, T> getAll(String name, Class<T> clazz) {
        String futureId = Utils.getAlphaNumericString(128);
        CompletableFuture<HashMap<String, T>> completableFuture = new CompletableFuture<>();
        try {
            cacheCallbackMap.put(futureId, completableFuture);
            netCacheClient.kryoClient().send(new PacketRequestCacheGetAll(futureId, name), true);
            return completableFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public <T> HashMap<String, T> getAllAsync(String name, Class<T> clazz) {
        try {
            return (HashMap<String, T>) executor.submit(() -> {
                String futureId = Utils.getAlphaNumericString(128);
                CompletableFuture<HashMap<String, T>> completableFuture = new CompletableFuture<>();
                try {
                    cacheCallbackMap.put(futureId, completableFuture);
                    netCacheClient.kryoClient().send(new PacketRequestCacheGetAll(futureId, name), false);
                    return completableFuture.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new HashMap<>();
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
