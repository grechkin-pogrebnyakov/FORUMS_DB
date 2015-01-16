package dataSets;


/**
 * Created by serg on 21.11.14.
 */
public class UserDataSet {
    private long id;
    private String login;
    private String password;
    private String email;
    private long score;

    public UserDataSet(long id, String login, String password, String email, long score) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = score;
    }

    public UserDataSet(String login, String password, String email) {
        id = -1;
        this.login = login;
        this.password = password;
        this.email = email;
        score = 0;
    }

    public long getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public long getScore() {
        return this.score;
    }
}
