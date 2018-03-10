package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import entity.User;



public class UserDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;

    public UserDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
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
/*
    public User getUser(String email) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String password = resultSet.getString("password");
            String username = resultSet.getString("username");
            int status = resultSet.getInt("status");
            int c = resultSet.getInt("classification");
            user = new User(email,username, password,status,c);
        }

        resultSet.close();
        statement.close();

        return user;
    }
}*/
    public List<User> similarUser(String email)throws SQLException{
        List<User> result = new ArrayList<User>();
        String sql = "SELECT * FROM users WHERE classification = (SELECT classification from users where email = ?) and email != ?";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, email);
        statement.setString(2, email);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            String e = resultSet.getString("email");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            int status = resultSet.getInt("status");
            int c = resultSet.getInt("classification");
            User user = new User(e,username, password,status,c);
            result.add(user);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return result;
    }

    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (email,username,password,status,classification) VALUES (?,?,?,?, ?)";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1,user.getEmail());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.setInt(4, user.getStatus());
        statement.setInt(5, -1);//正的是聚过类的，-1表示待聚类
        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }

    public List<User> listAllUsers() throws SQLException {
        List<User> listUer = new ArrayList<User>();

        String sql = "SELECT * FROM users";

        connect();

        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            String email = resultSet.getString("email");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            int status = resultSet.getInt("status");
            int c = resultSet.getInt("classification");
            User user = new User(email,username, password,status,c);
            listUer.add(user);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return listUer;
    }

    public boolean deleteUser(User user) throws SQLException {
        String sql = "DELETE FROM users where email = ?";

        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, user.getEmail());
        boolean rowDeleted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowDeleted;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?,password = ?,status =?";
        sql += " WHERE email = ?";
        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setInt(3, user.getStatus());
        statement.setString(4, user.getEmail());
        boolean rowUpdated = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowUpdated;
    }

    public User getUser(String email) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String password = resultSet.getString("password");
            String username = resultSet.getString("username");
            int status = resultSet.getInt("status");
            int c = resultSet.getInt("classification");
            user = new User(email,username, password,status,c);
        }

        resultSet.close();
        statement.close();

        return user;
    }
}
