package dataSets;

/**
 * Created by serg on 21.11.14.
 */
public class ThreadDataSet {
    private int id;
    private String date;
    private String message;
    private String user;
    private String forum;
    private String title;
    private String slug;
    private int likes;
    private int dislikes;
    private int points;
    private boolean isClosed;
    private boolean isDeleted;

    public ThreadDataSet(int id, String date, int dislikes, String forum, boolean isClosed, boolean isDeleted,
                         int likes, String message, String slug, String title, String user) {
        this.id = id;
        this.date = date.substring(0, date.length() - 2);
        this.dislikes =  dislikes;
        this.forum = forum;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
        this.likes = likes;
        points = likes - dislikes;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.user = user;
    }

    public int GetId() {return id;}
    public String GetDate() {return date;}
    public String GetMessage() {return message;}
    public String GetUser() {return user;}
    public String GetForum() {return forum;}
    public String GetTitle() {return title;}
    public String GetSlug() {return  slug;}
    public int GetLikes() {return likes;}
    public int GetDislikes() {return dislikes;}
    public int GetPoints() {return points;}
    public boolean GetIsClosed() {return isClosed;}
    public boolean GetIsDeleted() {return isDeleted;}

}
