package utils;

/**
 * Created by serg on 02.11.14.
 */
public class ResponseMaker {
    private static ResponseMaker instance = new ResponseMaker();

    private ResponseMaker(){}

    public static ResponseMaker getInstance(){return instance;}

    public String makeResponse(int status, Object resp) {
        MyJSONObject json = new MyJSONObject();
        json.put("code", status);
        json.put("response", resp);
        return json.toString(5);
    }
}
