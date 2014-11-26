package frontend;

import base.DBServiceImpl;
import base.DatabaseService;
import utils.ResponseMaker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by serg on 01.11.14.
 */
public class ClearServletImpl extends HttpServlet {
    private DatabaseService databaseService;
    private ResponseMaker responseMaker;

    public ClearServletImpl() {
        this.databaseService= DBServiceImpl.getInstance();
        this.responseMaker = ResponseMaker.getInstance();
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        String result = databaseService.clear();
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        //response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().println(responseMaker.makeResponse(status, result));
    }

    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        String result = databaseService.clear();
        int status = 0;
        response.setStatus(HttpServletResponse.SC_OK);
        //response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().println(responseMaker.makeResponse(status, result));
    }
}