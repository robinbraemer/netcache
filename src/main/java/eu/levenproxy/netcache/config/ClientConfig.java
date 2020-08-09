package eu.levenproxy.netcache.config;

public class ClientConfig {

    String serverAddress;
    int serverPort;
    String memberName;

    public ClientConfig(String serverAddress, int serverPort, String memberName) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.memberName = memberName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

}
