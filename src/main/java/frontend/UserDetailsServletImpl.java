package frontend;

import base.DBServiceImpl;
import base.DatabaseService;
import utils.ResponseMaker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import utils.MyJSONObject;

/**
 * Created by serg on 01.11.14.
 */
public class UserDetailsServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public UserDetailsServletImpl() {
        this.databaseService = new DBServiceImpl();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/x-json");
        String email = request.getParameter("user");
        if (email == null) {
            status = 3;
            String st = ResponseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
//        System.out.append("user details request: " + request.getParameterMap().toString() + "\n");
        MyJSONObject resp = databaseService.userDetails(email);
        String st;
        if (resp == null) {
            status = 1;
            st = ResponseMaker.makeResponse(status, "not found");
        } else {
            st = ResponseMaker.makeResponse(status, resp);
        }
//        System.out.append("user details response: " + st + "\n");
        response.getWriter().print(st);
        return;
    }
}
