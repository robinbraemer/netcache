package eu.levenproxy.netcache.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLStorage extends Storage {

    private final SQLDriver sqlDriver;

    public SQLStorage(SQLDriver sqlDriver, StorageCachePool cachePool, String name) {
        super(cachePool, name);
        this.sqlDriver = sqlDriver;
        this.sqlDriver.executeUpdate("CREATE TABLE IF NOT EXISTS " + name + " (`storage_key` TEXT, `storage_content` TEXT)");
    }

    @Override
    public String get(String key) {
        return getFromCache(key);
    }

    @Override
    public boolean contains(String key) {
        return isInCache(key);
    }

    @Override
    public void set(String key, String content) {
        try {
            PreparedStatement preparedStatement;
            if (contains(key)) {
                preparedStatement = sqlDriver.getConnection().prepareStatement("UPDATE `" + getName() + "` SET `storage_content`=? WHERE `storage_key`=?");
                preparedStatement.setString(1, content);
                preparedStatement.setString(2, key);
            } else {
                preparedStatement = sqlDriver.getConnection().prepareStatement("INSERT INTO `" + getName() + "` (`storage_key`, `storage_content`) VALUES (?, ?)");
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, content);
            }
            sqlDriver.executeUpdate(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setInCache(key, content);
    }

    @Override
    public void setAll(HashMap<String, String> hashMap) {
        HashMap<String, String> copy = new HashMap<>(hashMap);
        getCachePool().run(() -> {
            try {
                PreparedStatement preparedStatement = sqlDriver.getConnection().prepareStatement("INSERT INTO `" + getName() + "` (`storage_key`, `storage_content`) VALUES (?, ?)");
                AtomicInteger count = new AtomicInteger(0);
                copy.forEach((key, value) -> {
                    try {
                        preparedStatement.setString(1, key);
                        preparedStatement.setString(2, value);
                        preparedStatement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                preparedStatement.executeLargeBatch();
                copy.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        setAllInCache(hashMap);
        hashMap.clear();
    }

    @Override
    public HashMap<String, String> getAll() {
        return getAllFromCache();
    }

    @Override
    public void delete(String key) {
        deleteFromCache(key);
        getCachePool().run(() -> {
            try {
                PreparedStatement preparedStatement = sqlDriver.getConnection().prepareStatement("DELETE FROM `" + getName() + "` WHERE `storage_key`=?");
                preparedStatement.setString(1, key);
                sqlDriver.executeUpdate(preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public HashMap<String, String> loadContent() {
        HashMap<String, String> contentMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = sqlDriver.getConnection().prepareStatement("SELECT * FROM `" + getName() + "`");
            ResultSet resultSet = sqlDriver.executeQuery(preparedStatement);
            while (resultSet.next()) {
                String key = resultSet.getString("storage_key");
                String content = resultSet.getString("storage_content");
                contentMap.put(key, content);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentMap;
    }
}
