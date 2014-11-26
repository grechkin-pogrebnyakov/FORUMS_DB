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
public class ThreadCreateServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public ThreadCreateServletImpl() {
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
//            System.out.append("thread create request: " + foo.toString() + "\n");
            String forum = foo.get("forum");
            String title = foo.get("title");
            String closed = foo.get("isClosed");
            Boolean isClosed = null;
            if (closed.equals("false")) {
                isClosed = new Boolean(false);
            } else if (closed.equals("true")){
                isClosed = new Boolean(true);
            }
            String deleted = foo.get("isDeleted");
            Boolean isDeleted = null;
            if (deleted == null || deleted.equals("false")) {
                isDeleted = new Boolean(false);
            } else if (deleted.equals("true")){
                isDeleted = new Boolean(true);
            }
            String user = foo.get("user");
            String date = foo.get("date");
            String message = foo.get("message");
            String slug = foo.get("slug");
            if (forum == null || title == null || isClosed == null || isDeleted == null ||
                    user == null || date == null || message == null || slug == null) {
                status = 3;
                String st = responseMaker.makeResponse(status, "not all required fields set");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.createThread(forum, title, isClosed, isDeleted, user, date, message, slug);
            String st;
            if (resp == null) {
                status = 5;
                st = responseMaker.makeResponse(status, "user exists");
            } else {
                st = responseMaker.makeResponse(status, resp);
            }

            //    String st = "{\"code\": 0, \"response\": {\"about\": \"" + about + "\", \"email\": \""+email+"\", \"id\": 1, \"isAnonymous\": "+anonymous.toString()+", \"name\": \""+name+"\", \"username\": \""+username+"\"}}";

            response.getWriter().print(st);
        } else {
            status = 2;
            String st = responseMaker.makeResponse(status, "impossible to parse json");
            response.getWriter().print(st);
        }
    }
}