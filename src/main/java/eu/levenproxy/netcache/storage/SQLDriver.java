package eu.levenproxy.netcache.storage;

import eu.levenproxy.netcache.utils.SQLCredentials;

import java.sql.*;
import java.util.ArrayList;

public class SQLDriver {

    private String host;
    private int port;
    private String user;
    private String pass;
    private String database;
    private int fetchSize;
    private Connection conn;

    public SQLDriver(SQLCredentials sqlCredentials) {
        this.host = sqlCredentials.getHost();
        this.port = sqlCredentials.getPort();
        this.user = sqlCredentials.getUser();
        this.pass = sqlCredentials.getPassword();
        this.database = sqlCredentials.getDatabase();
        this.fetchSize = 2000;
        start();
    }

    public void start() {
        try {
            if ((this.conn == null)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&rewriteBatchedStatements=true&useCursorFetch=true";
                this.conn = DriverManager.getConnection(url, this.user, this.pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (this.conn == null) {
                if (this.conn.isClosed()) {
                    start();
                }
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return this.conn;
    }

    public void stop() {
        try {
            if (this.conn != null) {
                if (this.conn.isClosed()) {
                    this.conn.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(PreparedStatement st, ResultSet rs) {
        try {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void executeUpdate(String statement) {
        try {
            PreparedStatement st = this.conn.prepareStatement(statement);
            st.executeUpdate();
            close(st, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeUpdate(PreparedStatement statement) {
        try {
            statement.executeUpdate();
            close(statement, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String statement) {
        try {
            PreparedStatement st = this.conn.prepareStatement(statement);
            return st.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet executeQuery(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getTableNames() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT table_name FROM information_schema.tables where table_schema=?");
            preparedStatement.setString(1, database);
            ResultSet resultSet = executeQuery(preparedStatement);
            while(resultSet.next()) {
                arrayList.add(resultSet.getString("table_name"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

}
