package dataSets;

/**
 * Created by serg on 21.11.14.
 */
public class ForumDataSet {
    private int id;
    private String name;
    private String short_name;
    private String user;

    public ForumDataSet(int id, String short_name, String name, String user) {
        this.id = id;
        this.short_name = short_name;
        this.name = name;
        this.user = user;
    }

    public int GetId() {return id;}
    public String GetName() {return name;}
    public String GetShort_name() {return short_name;}
    public String GetUser() {return user;}

}
