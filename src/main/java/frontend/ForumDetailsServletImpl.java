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
    private ResponseMaker responseMaker;

    public ForumDetailsServletImpl() {
        this.databaseService= DBServiceImpl.getInstance();
        this.responseMaker = ResponseMaker.getInstance();
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
            String st = responseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        boolean rel_user = false;
        if (ru.equals("user")) {
            rel_user = true;
        }
//        System.out.append("forum details request: " + request.getParameterMap().toString() + "\n");
        MyJSONObject resp = databaseService.forumDetails(short_name, rel_user);
        String st;
        if (resp == null) {
            status = 1;
            st = responseMaker.makeResponse(status, "not found");
        } else {
            st = responseMaker.makeResponse(status, resp);
        }
//        System.out.append("forum details response: " + st + "\n");
        response.getWriter().print(st);
        return;
    }
}