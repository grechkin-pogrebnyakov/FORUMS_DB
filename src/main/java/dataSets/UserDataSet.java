package dataSets;


import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by serg on 21.11.14.
 */
public class UserDataSet {
    private int id;
    private String email;
    private String about;
    private boolean isAn;
    private String name;
    private String username;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    private ArrayList<Integer> subscr;

    public UserDataSet(int id, String username, String email, String about, boolean isAn,
                       String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.about = about;
        this.isAn = isAn;
        this.name = name;
    }

    public void SetFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public void SetFollowings(ArrayList<String> following) {
        this.following = following;
    }

    public void SetSubscr(ArrayList<Integer> subscr) {
        this.subscr = subscr;
    }

    public int GetId() {return id;}
    public String GetEmail() {return email;}
    public String GetAbout() {return about;}
    public boolean GetIsAn() {return isAn;}
    public String GetName() {return name;}
    public String GetUsername() {return username;}
    public ArrayList GetFollowers() {return followers;}
    public ArrayList GetFollowing() {return following;}
    public ArrayList GetSubscr() {return subscr;}

}
