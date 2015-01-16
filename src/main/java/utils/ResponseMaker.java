package utils;

/**
 * Created by serg on 02.11.14.
 */
public class ResponseMaker {
    public static String makeResponse(int status, Object resp) {
    //    MyJSONObject json = new MyJSONObject();
        //json.put("code", status);
        //json.put("response", resp);
        //return json.toString(5);
        StringBuilder res = new StringBuilder();
        res.append("{\"code\": ");
        res.append(status);
        res.append(", \"response\": ");
        if (resp instanceof String) {
            res.append("\"");
            res.append(resp.toString());
            res.append("\"");
        } else {
            res.append(resp.toString());
        }
        res.append("}");
        return res.toString();
    }
}
