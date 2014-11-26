package frontend;

import base.DBServiceImpl;
import base.DatabaseService;
import org.json.JSONArray;
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
public class ForumListPostsServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public ForumListPostsServletImpl() {
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
            String st = responseMaker.makeResponse(status, "incorrect request");
            response.getWriter().println(st);
            return;
        }
        String[] related = request.getParameterValues("related");
        boolean rel_user = false;
        boolean rel_forum = false;
        boolean rel_thread = false;
        if (related != null)
            for (String sti : related) {
                if (sti.equals("user")) {
                    rel_user = true;
                } else if (sti.equals("forum")) {
                    rel_forum = true;
                } else if (sti.equals("thread")) {
                    rel_thread = true;
                }
            }
//        System.out.append("forum post list request: " + request.getParameterMap() + "\n");
        MyJSONArray resp = databaseService.postList(forum, false, rel_user, rel_forum, rel_thread, since, limit, isAsc);
        String st;
        st = responseMaker.makeResponse(status, resp);
//        System.out.append("forum post list response: " + st + "\n");
        response.getWriter().print(st);
    }

}