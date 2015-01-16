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
public class ThreadListPostsServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public ThreadListPostsServletImpl() {
        this.databaseService = new DBServiceImpl();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String thread = request.getParameter("thread");
        Integer thread_id;
        if (thread != null) {
            try {
                thread_id = new Integer(thread);
            } catch (NumberFormatException e){
                e.printStackTrace();
                status = 3;
                String st = ResponseMaker.makeResponse(status, "thread id required");
                response.getWriter().print(st);
                return;
            }
        } else {
            status = 3;
            String st = ResponseMaker.makeResponse(status, "thread id required");
            response.getWriter().print(st);
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
        String sort = request.getParameter("sort");
        if (sort == null) {
            sort = "flat";
        }
//        System.out.append("post list request: " + request.getParameterMap() + "\n");
        MyJSONArray resp;
        switch (sort) {
            case "flat" : {
                resp = databaseService.postList(thread, true, false, false, false, since, limit, isAsc);
                break;
            }
            case "tree" : {
                resp = databaseService.threadListPostsTree(thread_id, since, limit, isAsc);
                break;
            }
            case "parent_tree" : {
                resp = databaseService.threadListPostsParentTree(thread_id, since, limit, isAsc);
                break;
            }
            default : {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "incorrect request");
                response.getWriter().println(st);
                return;
            }
        }
        String st;
        st = ResponseMaker.makeResponse(status, resp);
//        System.out.append("post list response: " + st + "\n");
        response.getWriter().print(st);
    }

}