package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import entity.Record;
import entity.Time_record;
import entity.Weight;
import net.sf.json.JSONArray;

import entity.Appinfo;
import sun.security.krb5.internal.APOptions;


public class AppinfoDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;

    public AppinfoDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
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
    public boolean insertOrUpdateWeight(Weight w) throws SQLException {//每次插入/更新weight，要检查appinfo里是否有这个appinfo
        connect();
        System.out.println("insert appinfo");
        String sql1 = "INSERT INTO appinfo (packagename,appname,category,weight,installnum,minutes,image) VALUES (?, ?, ?, ? ,?, ?, ?) ON DUPLICATE KEY UPDATE installnum = installnum + 1";
        PreparedStatement statement1 = jdbcConnection.prepareStatement(sql1);
        statement1.setString(1, w.getPackagename());
        statement1.setString(2, w.getAppname());
        statement1.setInt(3, 0);
        statement1.setDouble(4,w.getWeight());
        statement1.setInt(5, 1);
        statement1.setInt(6, w.minutes);
        statement1.setString(7, "null");
        statement1.executeUpdate();
        statement1.close();

        //接下来是insert or update weight
        String sql2  = "INSERT INTO weight(email,packagename,weight) VALUES (?,?,?) ON DUPLICATE KEY UPDATE weight =?";
        PreparedStatement statement2 = jdbcConnection.prepareStatement(sql2);
        statement2.setInt(3,w.getWeight());
        statement2.setString(2,w.getPackagename());
        statement2.setString(1,w.getEmail());
        statement2.setInt(4,w.getWeight());

        System.out.println(statement2);
        boolean rowUpdated = statement2.executeUpdate() > 0 ;


        System.out.println("insert weight success");

        statement2.close();
        return true;
    }

    public boolean insertRecord(Record r) throws SQLException {
        connect();
        boolean rowInserted = false;
        java.sql.Date d = new Date(r.getDay().getTime());
        String sql2  = "INSERT INTO record(email,packagename,day,duration,frequency) values(?,?,?,?,?) ON DUPLICATE KEY UPDATE duration = ?,frequency = ?";
        PreparedStatement statement2 = jdbcConnection.prepareStatement(sql2);
        statement2.setString(1,r.getEmail());
        statement2.setString(2,r.getPackageName());
        statement2.setInt(4,r.getDuration());
        statement2.setInt(5,r.getFrequency());
        statement2.setDate(3,d);
        statement2.setInt(6,r.getDuration());
        statement2.setInt(7,r.getFrequency());
        boolean rowUpdated = statement2.executeUpdate() > 0 ;
        System.out.println("insert/update record success");
        disconnect();
        return rowUpdated;
    }

    public boolean insertAT(String email,java.util.Date d,float at) throws SQLException{
        connect();
        java.sql.Date date = new Date(d.getTime());
        String sql = "insert into at_record(email,day,at) values(?,?,?) ON DUPLICATE KEY UPDATE at =?";
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1,email);
        statement.setDate(2,date);
        statement.setFloat(3,at);
        statement.setFloat(4,at);
        boolean rowUpdated = statement.executeUpdate() > 0 ;
        System.out.println("insert/update at_record success");
        disconnect();
        return rowUpdated;
    }
    public List<Appinfo> listAllAppinfo() throws SQLException {
        List<Appinfo> listAppinfo = new ArrayList<Appinfo>();

        String sql = "SELECT * FROM appinfo";

        connect();

        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            String appname = resultSet.getString("appname");
            int category = resultSet.getInt("category");
            int weight = resultSet.getInt("weight");
            String packagename = resultSet.getString("packagename");
            String image = resultSet.getString("iamge");
            int installnum = resultSet.getInt("installnum");
            Appinfo appinfo;
            appinfo = new Appinfo(packagename,appname,category,weight,image,installnum);
            listAppinfo.add(appinfo);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return listAppinfo;
    }

    // 删除一个appinfo的感觉用不上，暂时没写

    public boolean updateAppinfo(Appinfo app) throws SQLException {
        String sql = "UPDATE appinfo SET weight = ?";
        sql += " WHERE appname = ?";
        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setDouble(1, app.getWeight());
        statement.setString(2, app.getAppname());
        boolean rowUpdated = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowUpdated;
    }

    public List<Appinfo> searchAppinfo(String appname)throws SQLException{
        List<Appinfo> list = new ArrayList<Appinfo>();
        String sql = "SELECT * FROM appinfo where appname like '%" + appname + "%'";
        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            //把resultset里的转换成json传回客户端
            String pack = resultSet.getString("packagename");
            String app = resultSet.getString("appname");
            int weight = resultSet.getInt("weight");
            int install = resultSet.getInt("installnum");
            int cate = resultSet.getInt("category");
            String image = resultSet.getString("image");
            int min =resultSet.getInt("minutes");
            Appinfo tmp = new Appinfo(pack,app,cate,weight,image,install,min);
            list.add(tmp);
          //  tmp.print();
        }

        resultSet.close();
        statement.close();
        disconnect();
        System.out.println("disconnect");
        return list;
    }
    public Appinfo getAppinfo(String packagename) throws SQLException {
        Appinfo appinfo = null;
        String sql = "SELECT * FROM appinfo WHERE packagename = ?";
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, packagename);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String appname = resultSet.getString("appname");
            int category = resultSet.getInt("category");
            int weight = resultSet.getInt("weight");
            String image = resultSet.getString("iamge");
            int installnum = resultSet.getInt("installnum");
            int min =resultSet.getInt("minutes");
            appinfo = new Appinfo(packagename,appname,category,weight,image,installnum,min);
        }

        resultSet.close();
        statement.close();

        return appinfo;
    }

    public double getAvgMin(String email)throws SQLException{
        connect();
        double min = 0;
        String sql ="select avg_min from stat where email =\'"+email + "\'";

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet r = statement.executeQuery();
        while(r.next()){
            min = r.getDouble("avg_min");

        }
        return min;
    }
    public double getAvgAT(String email)throws SQLException{
        connect();
        double at = 0;
        String sql ="select avg_at from stat where email =\'"+email + "\'";
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet r = statement.executeQuery();
        while(r.next()){
            at = r.getDouble("avg_at");
        }
        return at;
    }

    public List<Time_record> getTime(String email)throws SQLException{
        List<Time_record> list = new ArrayList<Time_record>();
        String sql = "select * from time_record where email =\'"+email+"\' order by minutes DESC ";
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            String pack = resultSet.getString("packagename");
            String app = resultSet.getString("appname");
            int min =resultSet.getInt("minutes");
            Time_record tmp = new Time_record(email,pack,app,min);
            list.add(tmp);
        }
        resultSet.close();
        statement.close();
        return list;

    }
    public List<Appinfo> listByInstall()throws SQLException{
        connect();
        List<Appinfo> list = new ArrayList<Appinfo>();//返回值
        String sql = "select * from rank_install order by installnum DESC";
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        int i =0;
        while(resultSet.next() && i < 50){
            //把resultset里的转换成json传回客户端
            String pack = resultSet.getString("packagename");
            String app = resultSet.getString("appname");
            int weight = resultSet.getInt("weight");
            int install = resultSet.getInt("installnum");
            int cate = resultSet.getInt("category");
            String image = resultSet.getString("image");
            int min =resultSet.getInt("minutes");
            Appinfo tmp = new Appinfo(pack,app,cate,weight,image,install,min);
            list.add(tmp);
            i++;
            //  tmp.print();
        }

        resultSet.close();
        statement.close();
        return list;
    }

    public List<Appinfo> listByMin()throws SQLException{
        connect();
        List<Appinfo> list = new ArrayList<Appinfo>();//返回值
        String sql = "select * from rank_minutes order by minutes DESC";
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.setFetchSize(50);
        while(resultSet.next()){
            //把resultset里的转换成json传回客户端
            String pack = resultSet.getString("packagename");
            String app = resultSet.getString("appname");
            int weight = resultSet.getInt("weight");
            int install = resultSet.getInt("installnum");
            int cate = resultSet.getInt("category");
            String image = resultSet.getString("image");
            int min =resultSet.getInt("minutes");
            Appinfo tmp = new Appinfo(pack,app,cate,weight,image,install,min);
            list.add(tmp);
            //  tmp.print();
        }
        resultSet.close();
        statement.close();
        return list;
    }
    public List<Appinfo> recommendApp(String email)throws SQLException{
        connect();
        List<Appinfo> list = new ArrayList<Appinfo>();
        List<String> packagenameList = new ArrayList<String>();
        String sql = "select distinct packagename from weight where email in (select email from users where classification = (select classification from users where email ='" +email+ " ')) and packagename in (select packagename from feature)" ;
        System.out.println(sql);
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        int i = 0;
        while(resultSet.next() && i < 50){
            String tmp = resultSet.getString("packagename");
            packagenameList.add(tmp);
            i++;
        }

        i =0;
        while(i < packagenameList.size()){//遍历packagenameList
            sql = "SELECT  * FROM appinfo WHERE packagename = '"+ packagenameList.get(i) + "'" ; //条件语句有问题
            statement = jdbcConnection.prepareStatement(sql);
            ResultSet r = statement.executeQuery();
            while(r.next()){
                String pack = r.getString("packagename");
                String app = r.getString("appname");
                int weight = r.getInt("weight");
                int install = r.getInt("installnum");
                int cate = r.getInt("category");
                int min =r.getInt("minutes");
                Appinfo tmp = new Appinfo(pack,app,cate,weight,"null",install,min);
                list.add(tmp);
            }
            i = i + 1;

            r.close();
        }
        resultSet.close();

        statement.close();
        disconnect();
        System.out.println("disconnect");
        return list;
    }
    public List<Appinfo> featureAppinfo(String email) throws SQLException{
        List<Appinfo> list = new ArrayList<Appinfo>();
        List<String> packagenameList = new ArrayList<String>();
        String sql = "SELECT packagename from weight WHERE email = " + email;
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            String tmp = resultSet.getString("packagenmae");
            packagenameList.add(tmp);
        }
        int i =0;
        while(i < packagenameList.size()){//遍历packagenameList
            sql = "SELECT  * FROM appinfo WHERE packagename = "+ packagenameList.get(i) +"AND packagename in (select packagename from feature)"; //条件语句有问题
            statement = jdbcConnection.prepareStatement(sql);
           ResultSet r = statement.executeQuery();
           while(r.next()){
               String pack = resultSet.getString("packagename");
               String app = resultSet.getString("appname");
               int weight = resultSet.getInt("weight");
               int install = resultSet.getInt("installnum");
               int cate = resultSet.getInt("category");
               String image = resultSet.getString("image");
               Appinfo tmp = new Appinfo(pack,app,cate,weight,image,install);
               list.add(tmp);
           }
           r.close();
        }
        resultSet.close();

        statement.close();
        disconnect();
        System.out.println("disconnect");
        return list;
    }
}
