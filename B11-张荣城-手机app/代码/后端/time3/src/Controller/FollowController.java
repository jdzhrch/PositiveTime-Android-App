package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import entity.Appinfo;
import entity.Follow;
import DAO.FollowDAO;
import entity.User;

import java.util.Date;
public class FollowController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FollowDAO followDAO;
    public void init() {
        String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
        followDAO = new FollowDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        System.out.println(action);
        try {
            switch (action) {
                case "/insert_follow":
                    insertFollow(request, response);
                    break;
                case "/delete_follow":
                    deleteFollow(request,response);
                    break;
                case "/is_follow":
                    isFollow(request,response);
                    break;
                case "/all":
                    listAll(request,response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request,response);

    }
    private void isFollow(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
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
        String following = input.split(":")[2].split("\"")[1];
        String email = t[1];

        Follow f = new Follow(email,following);
        boolean flag = followDAO.isFollow(f);
        if(flag){
            response.getOutputStream().println(1);
        }else{
            response.getOutputStream().println(0);
        }


    }
    private void deleteFollow(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String email = request.getParameter("email");
        String following = request.getParameter("following");
        Follow f =new Follow(email,following);
        followDAO.deleteFollow(f);

    }

    private void listAll(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
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
        List<User> list = followDAO.listAllFollow(email);
        String result = gson.toJson(list);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应

    }
    private void insertFollow(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        System.out.println("INSERT FOLLOW");
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
        String following = input.split(":")[2].split("\"")[1];
        String email = t[1];

        Follow f = new Follow(email,following);
        boolean flag = followDAO.insertFollow(f);

        if(flag){
            response.getOutputStream().println(1);
        }else{
            response.getOutputStream().println(0);
        }

        //给客户端写回成功标志用来识别


    }
}


