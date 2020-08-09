package eu.levenproxy.netcache.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import eu.levenproxy.netcache.client.CacheClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;

public class StorageCodec {

    private final CacheClient cacheClient;

    public StorageCodec(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    public <T> String encodeToBase64(T object) {
        byte[] bytes = translateTo(object);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public <T> T decodeFromBase64(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return (T) translateFrom(bytes);
    }

    public <T> HashMap<String, String> encodeToBase64Map(HashMap<String, T> hashMap) {
        HashMap<String, String> contentMap = new HashMap<>();
        hashMap.forEach((key, value) -> contentMap.put(key, encodeToBase64(value)));
        return contentMap;
    }

    public <T> HashMap<String, T> decodeFromBase64Map(HashMap<String, String> base64Map) {
        HashMap<String, T> contentMap = new HashMap<>();
        base64Map.forEach((key, value) -> contentMap.put(key, decodeFromBase64(value)));
        return contentMap;
    }

    public <T> byte[] translateTo(T object) {
        Kryo kryo = cacheClient.kryoClient().kryoSerialization().getKryo();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = cacheClient.kryoClient().kryoSerialization().getOutput();
        output.setOutputStream(outputStream);
        kryo.writeClassAndObject(output, object);
        output.flush();
        output.close();
        cacheClient.kryoClient().kryoSerialization().freeKryo(kryo);
        cacheClient.kryoClient().kryoSerialization().freeOutput(output);
        return outputStream.toByteArray();
    }

    public <T> T translateFrom(byte[] bytes) {
        Kryo kryo = cacheClient.kryoClient().kryoSerialization().getKryo();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input input = cacheClient.kryoClient().kryoSerialization().getInput();
        input.setInputStream(inputStream);
        T object = (T) kryo.readClassAndObject(input);
        input.close();
        cacheClient.kryoClient().kryoSerialization().freeKryo(kryo);
        cacheClient.kryoClient().kryoSerialization().freeInput(input);
        return object;
    }

}
