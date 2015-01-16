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
public class UserUpdateProfileServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public UserUpdateProfileServletImpl() {
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
//            System.out.append("thread create request: " + foo.toString() + "\n");
            String email = foo.get("user");

            String name = foo.get("name");
            String about = foo.get("about");
            if (email == null) {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "email required");
                response.getWriter().print(st);
                return;
            }
            if (name == null) {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "name required");
                response.getWriter().print(st);
                return;
            }
            if (about == null) {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "about required");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.updateUserProfile(email, name, about);
            String st;
            if (resp == null) {
                status = 1;
                st = ResponseMaker.makeResponse(status, "thread not found");
            } else {
                st = ResponseMaker.makeResponse(status, resp);
            }

            response.getWriter().print(st);
        } else {
            status = 2;
            String st = ResponseMaker.makeResponse(status, "impossible to parse json");
            response.getWriter().print(st);
        }
    }
}