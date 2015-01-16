package dataSets;

/**
 * Created by serg on 16.01.15.
 */
public class PostDataSet {
    private int id;
    private String date;
    private int dislikes;
    private String forum;
    private boolean isApproved;
    private boolean isDeleted;
    private boolean isEdited;
    private boolean isHighlighted;
    private boolean isSpam;
    private int likes;
    private String message;
    private Integer parent;
    private int points;
    private int thread;
    private String user;

    public PostDataSet(int id, String date, int dislikes, String forum, boolean isApproved, boolean isDeleted,
                       boolean isEdited, boolean isHighlighted, boolean isSpam, int likes,
                       String message, Integer parent, int thread, String user) {
        this.id = id;
        this.date = date.substring(0, date.length() - 2);
        this.dislikes =  dislikes;
        this.forum = forum;
        this.isApproved = isApproved;
        this.isDeleted = isDeleted;
        this.isEdited = isEdited;
        this.isHighlighted = isHighlighted;
        this.isSpam = isSpam;
        this.likes = likes;
        points = likes - dislikes;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.user = user;
    }

    public int GetId() {return id;}
    public String GetDate() {return date;}
    public String GetMessage() {return message;}
    public String GetUser() {return user;}
    public String GetForum() {return forum;}
    public int GetThread() {return thread;}
    public Integer GetParent() {return  parent;}
    public int GetLikes() {return likes;}
    public int GetDislikes() {return dislikes;}
    public int GetPoints() {return points;}
    public boolean GetIsApproved() {return isApproved;}
    public boolean GetIsDeleted() {return isDeleted;}
    public boolean GetIsEdited() {return isEdited;}
    public boolean GetIsHighlighted() {return isHighlighted;}
    public boolean GetIsSpam() {return isSpam;}

}

