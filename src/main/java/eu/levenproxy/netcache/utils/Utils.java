package eu.levenproxy.netcache.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().create();
    }

    public static String getAlphaNumericString(int stringLength) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String generateMd5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(bytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateMd5(String stringToHash) {
        return generateMd5(stringToHash.getBytes());
    }

    public static String getRemoteIPv4() {
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String ip = in.readLine();
            System.out.println(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    public static Gson getGson() {
        return gson;
    }

    public static String wrapInetAddress(InetAddress address) {
        return address.toString().startsWith("/") ? address.toString().replaceFirst("/", "") : address.toString();
    }

    public static String wrapInetAddress(Channel channel) {
        if (channel != null && channel.remoteAddress() != null) {
            InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
            return wrapInetAddress(address);
        }
        return null;
    }

    public static String wrapInetAddress(ChannelHandlerContext ctx) {
        if(ctx != null && ctx.channel() != null) {
            return wrapInetAddress(ctx.channel());
        }
        return null;
    }

}
