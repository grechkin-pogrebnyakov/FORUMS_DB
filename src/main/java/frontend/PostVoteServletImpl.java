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
public class PostVoteServletImpl extends HttpServlet {
    private DatabaseService databaseService;

    public PostVoteServletImpl() {
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
//            System.out.append("post create request: " + foo.toString() + "\n");
            String post = foo.get("post");

            String vote_st = foo.get("vote");
            Integer post_id;
            if (post != null) {
                try {
                    post_id = new Integer(post);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    status = 3;
                    String st = ResponseMaker.makeResponse(status, "post id required");
                    response.getWriter().print(st);
                    return;
                }
            } else {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "post id required");
                response.getWriter().print(st);
                return;
            }
            Integer vote;
            if (vote_st != null) {
                try {
                    vote = new Integer(vote_st);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    status = 3;
                    String st = ResponseMaker.makeResponse(status, "vote required");
                    response.getWriter().print(st);
                    return;
                }
            } else {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "vote required");
                response.getWriter().print(st);
                return;
            }

            if (!vote.equals(-1) && !vote.equals(1)) {
                status = 3;
                String st = ResponseMaker.makeResponse(status, "vote required");
                response.getWriter().print(st);
                return;
            }

            MyJSONObject resp = databaseService.votePost(post_id, vote);
            String st;
            if (resp == null) {
                status = 1;
                st = ResponseMaker.makeResponse(status, "post not found");
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