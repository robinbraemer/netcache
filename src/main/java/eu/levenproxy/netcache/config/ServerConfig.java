package eu.levenproxy.netcache.config;

public class ServerConfig {

    int serverPort;

    public ServerConfig(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
