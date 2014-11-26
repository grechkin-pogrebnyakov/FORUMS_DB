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
 * Created by Serg on 01.11.14.
 */
public class UserCreateServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public UserCreateServletImpl() {
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
//            System.out.append("user create request: " + foo.toString() + "\n");
            String username = foo.get("username");
            String about = foo.get("about");
            String anon = foo.get("isAnonymous");
            Boolean anonymous = null;
            if (anon == null || anon.equals("false")) {
                anonymous = new Boolean(false);
            } else if (anon.equals("true")){
                anonymous = new Boolean(true);
            }
            String name = foo.get("name");
            String email = foo.get("email");
            if (email == null) {
                status = 3;
                String st = responseMaker.makeResponse(status, "email required");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.createUser(username,about,name,email,anonymous);
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