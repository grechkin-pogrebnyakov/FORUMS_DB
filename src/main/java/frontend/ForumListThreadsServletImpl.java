package frontend;

import base.DBServiceImpl;
import base.DatabaseService;
import utils.MyJSONArray;
import utils.ResponseMaker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by serg on 01.11.14.
 */
public class ForumListThreadsServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public ForumListThreadsServletImpl() {
        this.databaseService = new DBServiceImpl();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String forum = request.getParameter("forum");
        if (forum == null) {
            status = 3;
            String st = ResponseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        String since = request.getParameter("since");
        String limit_st = request.getParameter("limit");
        Integer limit;
        if (limit_st == null) {
            limit = null;
        } else {
            limit = new Integer(limit_st);
        }
        String order = request.getParameter("order");
        Boolean isAsc;
        if (order == null || order.equals("desc")) {
            isAsc = false;
        } else if (order.equals("asc")){
            isAsc = true;
        } else {
            status = 3;
            String st = ResponseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        String[] related = request.getParameterValues("related");
        boolean rel_user = false;
        boolean rel_forum = false;
        if (related != null)
            for (String sti : related) {
                if (sti.equals("user")) {
                    rel_user = true;
                } else if (sti.equals("forum")) {
                    rel_forum = true;
                }
            }
//        System.out.append("forum thread list request: " + request.getParameterMap() + "\n");
        MyJSONArray resp = databaseService.threadList(forum, false, rel_user, rel_forum, since, limit, isAsc);
        String st;
        st = ResponseMaker.makeResponse(status, resp);
//        System.out.append("forum thread list response: " + st + "\n");
        response.getWriter().print(st);
    }

}