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
public class ThreadUpdateServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public ThreadUpdateServletImpl() {
        this.databaseService= DBServiceImpl.getInstance();
        this.responseMaker = ResponseMaker.getInstance();
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/x-json");
        if (request.getContentType().equals("application/json")) {
            InputStream is = request.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> foo = mapper.readValue(is, new TypeReference<Map<String, String>>() {
            });
//            System.out.append("thread create request: " + foo.toString() + "\n");
            String thread = foo.get("thread");

            String message = foo.get("message");
            String slug = foo.get("slug");
            if (message == null) {
                status = 3;
                String st = responseMaker.makeResponse(status, "message required");
                response.getWriter().print(st);
                return;
            }
            if (slug == null) {
                status = 3;
                String st = responseMaker.makeResponse(status, "slug required");
                response.getWriter().print(st);
                return;
            }
            Integer thread_id;
            if (thread != null) {
                try {
                    thread_id = new Integer(thread);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    status = 3;
                    String st = responseMaker.makeResponse(status, "thread id required");
                    response.getWriter().print(st);
                    return;
                }
            } else {
                status = 3;
                String st = responseMaker.makeResponse(status, "thread id required");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.updateThread(thread_id, message, slug);
            String st;
            if (resp == null) {
                status = 1;
                st = responseMaker.makeResponse(status, "thread not found");
            } else {
                st = responseMaker.makeResponse(status, resp);
            }

            response.getWriter().print(st);
        } else {
            status = 2;
            String st = responseMaker.makeResponse(status, "impossible to parse json");
            response.getWriter().print(st);
        }
    }
}