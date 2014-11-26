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
public class ForumListUsersServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public ForumListUsersServletImpl() {
        this.databaseService= DBServiceImpl.getInstance();
        this.responseMaker = ResponseMaker.getInstance();
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
            String st = responseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        String since = request.getParameter("since_id");
        Integer since_id;
        if (since == null) {
            since_id = null;
        } else {
            since_id = new Integer(since);
        }
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
            String st = responseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
//        System.out.append("forum list users request: " + request.getParameterMap() + "\n");
        MyJSONArray resp = databaseService.forumListUsers(forum, since_id, limit, isAsc);
        String st;
        st = responseMaker.makeResponse(status, resp);
//        System.out.append("forum list users response: " + st + "\n");
        response.getWriter().print(st);
    }

}