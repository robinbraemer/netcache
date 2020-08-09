package eu.levenproxy.netcache.config;

import com.google.gson.Gson;

import java.io.*;

public class GsonConfig {

    public static boolean existsFile(String fileName) {
        return new File(fileName).exists();
    }

    public static void saveConfig(Gson gson, String fileName, Object object) {
        try {
            File configFile = new File(fileName);
            if (!configFile.exists() || object == null) {
                try {
                    configFile.createNewFile();
                    FileWriter writer = new FileWriter(configFile);
                    gson.toJson(object, writer);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    System.exit(444);
                }
                return;
            } else {
                gson.toJson(object, new FileWriter(fileName));
            }
        } catch (IOException e) {

        }
    }

    public static <T> T readConfig(Gson gson, String fileName, Class<T> clazzOfT, T defaultConfig) {
        try {
            File configFile = new File(fileName);
            if(!existsFile(fileName)) {
                saveConfig(gson, fileName, defaultConfig);
            }
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            T configuration = gson.fromJson(reader, clazzOfT);
            reader.close();
            return configuration;
        } catch (IOException e) {
        }
        return null;
    }
}
