package Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.regexp.internal.RE;
import entity.Record;
import DAO.RecordDAO;
import java.util.Date;

public class RecordController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RecordDAO followDAO;
    public void init() {
        String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
        followDAO = new RecordDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        try {
            switch (action) {
                case "/insert":
                    insertRecord(request,response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private void insertRecord(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        java.sql.Date day = strToDate(request.getParameter("day"));
        String email = request.getParameter("email");
        String packagename = request.getParameter("packagename");
        int frequency = Integer.parseInt(request.getParameter("frequency"));
        int duration = Integer.parseInt(request.getParameter("duration"));

        Record r = new Record(email,packagename,day,frequency,duration);
        boolean flag = followDAO.insertRecord(r);
        if(flag){
            System.out.println("insert ok");
        }

    }
    public static java.sql.Date strToDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }
}
