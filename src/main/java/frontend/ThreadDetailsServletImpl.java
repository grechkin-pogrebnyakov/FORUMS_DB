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
public class ThreadDetailsServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public ThreadDetailsServletImpl() {
        this.databaseService = new DBServiceImpl();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String thread = request.getParameter("thread");
        String[] related = request.getParameterValues("related");
        if (thread == null) {
            status = 3;
            String st = ResponseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        boolean rel_user = false;
        boolean rel_forum = false;
        if (related != null)
        for (String sti : related) {
            if (sti.equals("user")) {
                rel_user = true;
            } else if (sti.equals("forum")) {
                rel_forum = true;
            } else {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "incorrect request");
                response.getWriter().println(st);
                return;
            }
        }
        Integer thread_id = new Integer(thread);
//        System.out.append("thread details request: " + request.getParameterMap() + "\n");
        MyJSONObject resp = databaseService.threadDetails(thread_id, rel_user, rel_forum);
        String st;
        if (resp == null) {
            status = 1;
            st = ResponseMaker.makeResponse(status, "not found");
        } else {
            st = ResponseMaker.makeResponse(status, resp);
        }
//        System.out.append("thread details response: " + st + "\n");
        response.getWriter().print(st);
        return;
    }
}