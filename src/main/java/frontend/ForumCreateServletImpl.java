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
public class ForumCreateServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public ForumCreateServletImpl() {
        this.databaseService = new DBServiceImpl();
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
//            System.out.append("forum create request: " + foo.toString() + "\n");
            String name = foo.get("name");
            String short_name = foo.get("short_name");
            String user = foo.get("user");
            if (name == null || short_name == null || user == null) {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "email required");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.createForum(name, short_name, user);
            String st;
            if (resp == null) {
                status = 4;
                st = ResponseMaker.makeResponse(status, "we have got some problem");
            } else {
                st = ResponseMaker.makeResponse(status, resp);
            }
            response.getWriter().print(st);
            st = "sdfsf";
        } else {
            status = 2;
            String st = ResponseMaker.makeResponse(status, "impossible to parse json");
            response.getWriter().print(st);
        }

    }
}