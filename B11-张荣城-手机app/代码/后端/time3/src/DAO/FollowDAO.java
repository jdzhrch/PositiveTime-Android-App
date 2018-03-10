package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import entity.Follow;
import entity.User;

public class FollowDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;
    private UserDAO userDAO;
    public FollowDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
        userDAO = new UserDAO(jdbcURL,jdbcUsername,jdbcPassword);
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

    public boolean insertFollow(Follow f) throws SQLException {
        String sql = "INSERT INTO follow (email,following) VALUES (?, ?)";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1,f.getEmail());
        statement.setString(2, f.getFollowing());

        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }

    public boolean isFollow(Follow f)throws  SQLException{
        boolean flag = false;
        String sql = "select * from follow where email = ? and following = ?";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, f.getEmail());
        statement.setString(2, f.getFollowing());
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            flag = true;
        }
        resultSet.close();
        statement.close();
        disconnect();
        return flag;
    }
    public boolean deleteFollow(Follow f) throws SQLException {
        String sql = "DELETE FROM follow where email = ?";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, f.getEmail());
        boolean rowDeleted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowDeleted;
    }


    // 关注只有添加关注和取消关注
    public List<User> listAllFollow(String email) throws SQLException {
        List<User> listFollow = new ArrayList<User>();

        String sql = "SELECT * FROM follow where email = ?";

        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String e = resultSet.getString("following");
            System.out.println(e);
            User u = userDAO.getUser(e);
            listFollow.add(u);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return listFollow;
    }

}