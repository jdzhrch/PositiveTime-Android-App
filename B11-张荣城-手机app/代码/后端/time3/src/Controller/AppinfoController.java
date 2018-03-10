package Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletInputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import DAO.RecordDAO;
import com.google.gson.reflect.TypeToken;
import entity.*;
import DAO.AppinfoDAO;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONFunction;

import java.util.Date;
import java.util.SimpleTimeZone;

public class AppinfoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AppinfoDAO appinfoDAO;

    public void init() {
        String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
        appinfoDAO = new AppinfoDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        System.out.println(action);
        try {
            switch (action) {
                case "/search":
                    searchAppinfo(request,response);
                    break;
                case "/recommand":
                    recommend(request,response);//传入Email，给该用户传回一个推荐app的列表，按推荐顺序从高到低
                    break;
                case "/userApp"://返回用户的app和使用时间做成气泡图
                    userApp(request,response);
                    break;
                case "/rankByInstallnum":
                    rankByInstall(request,response);
                    break;
                case "/rankByMinutes":
                    rankByMinutes(request,response);
                    break;
                case "/insert":
                    insert(request,response);//一并插入record，weight
                    break;
                case "/userInfo":
                    userInfo(request,response);
                    break;
                case "/test":
                    test(request,response);
                    break;

            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        System.out.println(action);
        try {
            switch (action) {
                case "/test":
                    test(request,response);
                    break;

            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void userApp(HttpServletRequest request, HttpServletResponse response)//为气泡图准备，Android端接收appname和平均使用时长
            throws SQLException, IOException{
                System.out.println("bubble activity");
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
                BufferedReader br = new BufferedReader(new InputStreamReader(//使用字符流读取客户端发过来的数据
                        request.getInputStream()));
                String line = null;
                StringBuffer s = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    s.append(line);
                }
                br.close();
                String input = s.toString();
                input = URLDecoder.decode(input,"UTF-8");//客户端encode了，服务器decode才能确保有些字符没有被变乱
                Gson gson = new Gson();
                String[] t = input.split(":")[1].split("\"");
                String email = t[1];
                List<Time_record> list = new ArrayList<Time_record>();
                list = appinfoDAO.getTime(email);
                String result = gson.toJson(list);
                response.setContentType("application/json;charset=UTF-8");
                response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应

            }
    private void userInfo(HttpServletRequest request, HttpServletResponse response)//返回个人信息的平均at和日手机用时
            throws SQLException, IOException{

                double at = 0,min = 0;
                System.out.println("at and min");
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
                BufferedReader br = new BufferedReader(new InputStreamReader(//使用字符流读取客户端发过来的数据
                        request.getInputStream()));
                String line = null;
                StringBuffer s = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    s.append(line);
                }
                br.close();
                String input = s.toString();
                input = URLDecoder.decode(input,"UTF-8");//客户端encode了，服务器decode才能确保有些字符没有被变乱
                Gson gson = new Gson();
                String[] t = input.split(":")[1].split("\"");
                String email = t[1];
                System.out.println(email);
                at = appinfoDAO.getAvgAT(email);
                min = appinfoDAO.getAvgMin(email);
                avg tmp = new avg();
                tmp.avg_at = at;
                tmp.avg_min = min;
                String result = gson.toJson(tmp);
                System.out.println(result);
                response.setContentType("application/json;charset=UTF-8");
                response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应

    }

    private void rankByInstall(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException{

                Gson gson = new Gson();
                List<Appinfo> list = appinfoDAO.listByInstall();
                String result = gson.toJson(list);
                response.setContentType("application/json;charset=UTF-8");
                response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应

    }

    private void test(HttpServletRequest request, HttpServletResponse response)//插weight,record,appinfo
            throws SQLException, IOException{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                dateFormat.format(date); //2016/11/16 12:08:43
                System.out.println(date);
                Record r = new Record("123@qq.com","uiuc",date,123,123);
                appinfoDAO.insertRecord(r);
                System.out.println("insert success");
            }
    private void insert(HttpServletRequest request, HttpServletResponse response)//插weight,record,appinfo
            throws SQLException, IOException{
                Gson gson = new Gson();
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
                BufferedReader br = new BufferedReader(new InputStreamReader(//使用字符流读取客户端发过来的数据
                        request.getInputStream()));
                String line = null;
                StringBuffer s = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    s.append(line);
                }
                br.close();
                String input = s.toString();
                String[] re = input.split("at_yesterday:");
                List<UploadInfo> list = gson.fromJson(re[0],new TypeToken<ArrayList<UploadInfo>>(){}.getType());
                float at = Integer.parseInt(re[1]);

                for(int i=0;i < list.size();i++){
                    UploadInfo tmp = list.get(i);
                    System.out.println(tmp.packageName);
                    Date d = new Date();
                    DateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
                    try{
                        d = sdf.parse(tmp.day);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("parse ok");
                    Record r = new Record(tmp.email,tmp.packageName,d,tmp.frequency,tmp.duration);
                    Weight w = new Weight(tmp.email,tmp.packageName,tmp.appname,tmp.weight,tmp.duration);
                    System.out.println("new ok");
                    appinfoDAO.insertOrUpdateWeight(w);
                    appinfoDAO.insertRecord(r);
                    appinfoDAO.insertAT(tmp.email,d,at);
                    System.out.println(at);
                    System.out.println("insert ok");

                }
                System.out.println("insert record");

    }

    private void rankByMinutes(HttpServletRequest request, HttpServletResponse response)//为气泡图准备，Android端接收appname和平均使用时长
            throws SQLException, IOException{
                Gson gson = new Gson();
                List<Appinfo> list = appinfoDAO.listByMin();
                String result = gson.toJson(list);
                response.setContentType("application/json;charset=UTF-8");
                response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应
    }
    private void recommend(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException{
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(//使用字符流读取客户端发过来的数据
                request.getInputStream()));
        String line = null;
        StringBuffer s = new StringBuffer();
        while ((line = br.readLine()) != null) {
            s.append(line);
        }
        br.close();
        String input = s.toString();
        input = URLDecoder.decode(input,"UTF-8");//客户端encode了，服务器decode才能确保有些字符没有被变乱
        Gson gson = new Gson();
        String[] t = input.split(":")[1].split("\"");
        String email = t[1];

        List<Appinfo> list = appinfoDAO.recommendApp(email);
        String result = gson.toJson(list);
        System.out.println(result);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应


    }

    private void searchAppinfo(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException{
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(//使用字符流读取客户端发过来的数据
                request.getInputStream()));
        String line = null;
        StringBuffer s = new StringBuffer();
        while ((line = br.readLine()) != null) {
            s.append(line);
        }
        br.close();
        String input = s.toString();
        Gson gson = new Gson();
        String[] t = input.split(":")[1].split("\"");
        String appname = t[1];
        List<Appinfo> list = appinfoDAO.searchAppinfo(appname);
        String result = gson.toJson(list);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应
        System.out.println(request);
    }
    //不再写update的函数，只有weight和installnum需要更改，这块我额外写一个函数每天0点执行，更新一下整个表


}