package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
import entity.User;
import DAO.UserDAO;
import java.util.Date;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;
    public void init() {
        String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
        userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();

        try {
            switch (action) {
                case "/similarUser":
                    similarUser(request, response);
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
                case "/insert_user":
                    insertUser(request, response);
                    break;
                case "/update_user":
                    updateUser(request,response);
                    break;
                case "/login":
                    checkUser(request,response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }


    private void similarUser(HttpServletRequest request, HttpServletResponse response)
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
        List<User> list = userDAO.similarUser(email);
        String result = gson.toJson(list);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(result.getBytes("UTF-8"));//向客户端发送一个带有json对象内容的响应
    }

    private void checkUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User true_user = userDAO.getUser(email);
        String p2 = true_user.getPassword();
        if(password.equals(p2)){
            response.getOutputStream().println(1);
            System.out.println("login success");
        }else{
            response.getOutputStream().println(0);
            System.out.println("login failure");
        }
    }
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String username = request.getParameter("username");
        int status = 0;
        User user = new User(email,username,password,status);
        userDAO.updateUser(user);

    }
    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        System.out.println("register start");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String username = null;
        int status = 0;
        User test = userDAO.getUser(email);
        if(test == null) {
            User newUser = new User(email,username, password,status);
            userDAO.insertUser(newUser);
            response.getOutputStream().println(1);
            System.out.println("register success");
        }else{
            response.getOutputStream().println(0);
            System.out.println("register failure");
        }

    }
}