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
public class PostCreateServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public PostCreateServletImpl() {
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
//            System.out.append("post create request: " + foo.toString() + "\n");
            String forum = foo.get("forum");
            Integer thread = new Integer(foo.get("thread"));
            String parent_st = foo.get("parent");
            Integer parent = 0;
            if (parent_st != null) {
                parent = new Integer(parent_st);
            }
            if (parent.intValue() < 0) {parent = 0; }
            String deleted = foo.get("isDeleted");
            Boolean isDeleted = null;
            if (deleted == null || deleted.equals("false")) {
                isDeleted = new Boolean(false);
            } else if (deleted.equals("true")){
                isDeleted = new Boolean(true);
            }
            String approved = foo.get("isApproved");
            Boolean isApproved = null;
            if (approved == null || approved.equals("false")) {
                isApproved = new Boolean(false);
            } else if (approved.equals("true")){
                isApproved = new Boolean(true);
            }
            String highlighted = foo.get("isHighlighted");
            Boolean isHighlighted = null;
            if (highlighted == null || highlighted.equals("false")) {
                isHighlighted = new Boolean(false);
            } else if (highlighted.equals("true")){
                isHighlighted = new Boolean(true);
            }
            String edited = foo.get("isEdited");
            Boolean isEdited = null;
            if (edited == null || edited.equals("false")) {
                isEdited = new Boolean(false);
            } else if (edited.equals("true")){
                isEdited = new Boolean(true);
            }
            String spam = foo.get("isSpam");
            Boolean isSpam = null;
            if (spam == null || spam.equals("false")) {
                isSpam = new Boolean(false);
            } else if (spam.equals("true")){
                isSpam = new Boolean(true);
            }
            String user = foo.get("user");
            String date = foo.get("date");
            String message = foo.get("message");
            if (forum == null || thread == null || user == null || date == null || message == null) {
                status = 3;
                String st = responseMaker.makeResponse(status, "not all required fields set");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.createPost(forum, thread, parent, isDeleted, isApproved, isEdited, isHighlighted, isSpam, user, date, message);
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