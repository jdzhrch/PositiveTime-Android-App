package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import entity.Record;
import java.sql.Date;


public class RecordDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;

    public RecordDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    protected void connect() throws SQLException {
        if (jdbcConnection == null || jdbcConnection.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
            jdbcConnection = DriverManager.getConnection(
                    jdbcURL, jdbcUsername, jdbcPassword);
        }
    }

    protected void disconnect() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
    }

    public boolean insertRecord(Record r) throws SQLException {
        connect();
        boolean rowInserted = false;
        Date d = new Date(r.getDay().getTime());
        String sql2  = "update record set duration = ?,frequency = ? where packagename = ? and email = ? and day = ?";
        PreparedStatement statement2 = jdbcConnection.prepareStatement(sql2);
        statement2.setInt(1,r.getDuration());
        statement2.setInt(2,r.getDuration());
        statement2.setString(3,r.getPackageName());
        statement2.setString(4,r.getEmail());
        statement2.setDate(5,d);
        boolean rowUpdated = statement2.executeUpdate() > 0 ;
        if(!rowUpdated){
        String sql = "INSERT INTO record(email,packagename,day,frequency,duration) VALUES (?,?,?,?,?)";

        java.sql.Date day = new java.sql.Date(r.getDay().getTime());
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1,r.getEmail());
        statement.setString(2, r.getPackageName());
        statement.setDate(3,day);
        statement.setInt(4, r.getFrequency());
        statement.setInt(5,r.getDuration());
        rowInserted = statement.executeUpdate() > 0;
        statement.close();
        }
        disconnect();
        return rowInserted;
    }




}
