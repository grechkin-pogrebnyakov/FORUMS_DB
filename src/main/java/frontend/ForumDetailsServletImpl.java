package frontend;

import base.DBServiceImpl;
import base.DatabaseService;
import utils.ResponseMaker;
import utils.MyJSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by serg on 01.11.14.
 */
public class ForumDetailsServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public ForumDetailsServletImpl() {
        this.databaseService = new DBServiceImpl();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String short_name = request.getParameter("forum");
        String ru = request.getParameter("related");
        if (short_name == null) {
            status = 3;
            String st = ResponseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        boolean rel_user = false;
        if (ru != null && ru.equals("user")) {
            rel_user = true;
        }
//        System.out.append("forum details request: " + request.getParameterMap().toString() + "\n");
        MyJSONObject resp = databaseService.forumDetails(short_name, rel_user);
        String st;
        if (resp == null) {
            status = 1;
            st = ResponseMaker.makeResponse(status, "not found");
        } else {
            st = ResponseMaker.makeResponse(status, resp);
        }
//        System.out.append("forum details response: " + st + "\n");
        response.getWriter().print(st);
        return;
    }
}