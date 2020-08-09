package eu.levenproxy.netcache.utils;

public class SQLCredentials {

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;

    public SQLCredentials(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
