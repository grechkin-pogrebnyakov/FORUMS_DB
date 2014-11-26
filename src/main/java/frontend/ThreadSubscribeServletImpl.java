package frontend;

import base.DBServiceImpl;
import base.DatabaseService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import utils.ResponseMaker;
import utils.MyJSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by serg on 01.11.14.
 */
public class ThreadSubscribeServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public ThreadSubscribeServletImpl() {
        this.databaseService= DBServiceImpl.getInstance();
        this.responseMaker = ResponseMaker.getInstance();
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        if (request.getContentType().equals("application/json")) {
            InputStream is = request.getInputStream();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> foo = mapper.readValue(is, new TypeReference<Map<String, String>>() {
            });
//            System.out.append("thread remove request: " + foo.toString() + "\n");
            String thread = foo.get("thread");
            Integer thread_id;
            if (thread != null) {
                thread_id = new Integer(thread);
            } else {
                status = 3;
                String st = responseMaker.makeResponse(status, "thread id required");
                response.getWriter().print(st);
                return;
            }
            String user = foo.get("user");
            if (user == null){
                status = 3;
                String st = responseMaker.makeResponse(status, "user required");
                response.getWriter().print(st);
                return;
            }

            databaseService.subscribeThread(thread_id, user);
            String st;
            MyJSONObject resp = new MyJSONObject();
            resp.put("thread", thread_id);
            resp.put("user", user);
            st = responseMaker.makeResponse(status, resp);
//            System.out.append("thread remove response: " + st + "\n");
            response.getWriter().print(st);
        } else {
            status = 2;
            String st = responseMaker.makeResponse(status, "impossible to parse json");
            response.getWriter().print(st);
        }
    }
}