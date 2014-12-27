package base;

import utils.MyJSONArray;
import utils.MyJSONObject;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Created by Serg on 013 13.09.14.
 */
 public class DBServiceImpl implements DatabaseService {

    private static DBServiceImpl instance = new DBServiceImpl();

    private Connection connection;

    private DBServiceImpl() {
        this.connection = getConnection();
    }

    public static DBServiceImpl getInstance() {
        return instance;
    }

    public static Connection getConnection() {
        try{
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").		//db type
                    append("localhost:"). 			//host name
                    append("3306/").				//port
                    append("FORUMS_TP");			//db name

            Properties properties=new Properties();
            properties.setProperty("user","forum_user");
            properties.setProperty("password","12345");
            properties.setProperty("useUnicode","true");
            properties.setProperty("characterEncoding","UTF8");
            //     append("user=forum_user&").			//login
            //     append("password=12345").		//password
            //     append("");
            System.out.append("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString(), properties);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String clear(){
        try{
            String update = "Truncate ?";
            Statement stmt = connection.createStatement();
            stmt.execute("Truncate Forums");
            stmt.execute("Truncate Users");
            stmt.execute("Truncate Threads");
            stmt.execute("Truncate Posts");
            stmt.execute("Truncate ParentPosts");
            stmt.execute("Truncate Subscribers");
            stmt.execute("Truncate Follows");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    public MyJSONObject createUser(String username ,String about, String name, String email, Boolean isAn){
        int id = 0;
        try{
            String update = "INSERT into Users(user_email, username, name, about, isAnonymous) values(?,?,?,?,?)";
            PreparedStatement stmt = connection.prepareStatement("SELECT Count(*) FROM Users WHERE user_email=?");
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();
            result.next();
            if (result.getInt(1) == 0) {
                result.close();
                stmt.close();
                stmt = connection.prepareStatement(update);
                stmt.setString(1, email);
                stmt.setString(2, username);
                stmt.setString(3, name);
                stmt.setString(4, about);
                stmt.setBoolean(5, isAn);
                stmt.executeUpdate();
                stmt.execute("SELECT LAST_INSERT_ID()");
                result = stmt.getResultSet();
                result.next();
                id = result.getInt(1);
            }
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (id == 0) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("about", about);
        resp.put("email", email);
        resp.put("id", id);
        resp.put("isAnonymous", isAn);
        resp.put("name", name);
        resp.put("username", username);
        return resp;
    }

    public MyJSONObject userDetails(String email){
        int id = 0;
        String about = "";
        boolean isAn = false;
        String name = "";
        String username = "";
        ArrayList<String> followers = new ArrayList<String>();
        ArrayList<String> following = new ArrayList<String>();
        ArrayList<Integer> subscr = new ArrayList<Integer>();

        try{
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Users WHERE user_email=?");
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {

                id = result.getInt("user_id");
                about = result.getString("about");
                isAn = result.getBoolean("isAnonymous");
                name = result.getString("name");
                username = result.getString("username");
                result.close();
                stmt.close();
                stmt = connection.prepareStatement("SELECT follower FROM Follows WHERE following=?");
                stmt.setString(1, email);
                result = stmt.executeQuery();
                while (result.next()){
                    followers.add(result.getString(1));
                }
                result.close();
                stmt.close();
                stmt = connection.prepareStatement("SELECT following FROM Follows WHERE follower=?");
                stmt.setString(1, email);
                result = stmt.executeQuery();
                while (result.next()){
                    following.add(result.getString(1));
                }
                result.close();
                stmt.close();
                stmt = connection.prepareStatement("SELECT thread_id FROM Subscribers WHERE user_email=?");
                stmt.setString(1, email);
                result = stmt.executeQuery();
                while (result.next()){
                    subscr.add(result.getInt(1));
                }
            }
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (id == 0) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("about", about);
        resp.put("email", email);
        resp.put("followers", followers);
        resp.put("following", following);
        resp.put("id", id);
        resp.put("isAnonymous", isAn);
        resp.put("name", name);
        resp.put("subscriptions", subscr);
        resp.put("username", username);
        return resp;
    }

    public MyJSONObject createForum(String name, String short_name, String user){
        int id = 0;
        try{
            String update = "INSERT into Forums(forum_name, forum_shortname, user_email) values(?,?,?)";
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM Forums WHERE forum_shortname=?");
            stmt.setString(1, short_name);
            ResultSet result = stmt.executeQuery();
            result.next();
            int cnt = result.getInt(1);
            result.close();
            stmt.close();
            if (cnt == 0) {
                stmt = connection.prepareStatement(update);
                stmt.setString(1, name);
                stmt.setString(2, short_name);
                stmt.setString(3, user);
                stmt.executeUpdate();
                stmt.execute("SELECT LAST_INSERT_ID()");
                result = stmt.getResultSet();
                result.next();
                id = result.getInt(1);
            } else {
                stmt = connection.prepareStatement("SELECT * FROM Forums WHERE forum_shortname=?");
                stmt.setString(1, short_name);
                result = stmt.executeQuery();
                result.next();
                id = result.getInt("forum_id");
                name = result.getString("forum_name");
                user = result.getString("user_email");
            }
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (id == 0) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("id", id);
        resp.put("name", name);
        resp.put("short_name", short_name);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject forumDetails (String short_name, boolean rel_user) {
        int id = 0;
        String name = "";
        Object user = null;
        try{
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Forums WHERE forum_shortname=?");
            stmt.setString(1, short_name);
            ResultSet result = stmt.executeQuery();
            if (!result.isLast()) {
                result.next();
                id = result.getInt("forum_id");
                name = result.getString("forum_name");
                if (rel_user) {
                    user = userDetails(result.getString("user_email"));
                } else {
                    user = result.getString("user_email");
                }
            }
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (id == 0) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("id", id);
        resp.put("name", name);
        resp.put("short_name", short_name);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject createThread(String forum, String title, Boolean isClosed, Boolean isDeleted,
                                     String user, String date, String message, String slug) {
        int id = 0;
        try{
//            connection.setAutoCommit(false);
            String update = "insert into Threads(created, message, slug, title, isClosed, isDeleted, forum_shortname, user_email) values(?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setString(1, date);
            stmt.setString(2, message);
            stmt.setString(3, slug);
            stmt.setString(4, title);
            stmt.setBoolean(5, isClosed);
            stmt.setBoolean(6, isDeleted);
            stmt.setString(7, forum);
            stmt.setString(8, user);
            stmt.executeUpdate();
//            connection.setAutoCommit(false);
//            connection.commit();
            stmt.execute("SELECT LAST_INSERT_ID()");
            ResultSet result = stmt.getResultSet();
            result.next();
            id = result.getInt(1);
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (id == 0) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", date);
        resp.put("forum", forum);
        resp.put("id", id);
        resp.put("isClosed", isClosed);
        resp.put("isDeleted", isDeleted);
        resp.put("message", message);
        resp.put("slug", slug);
        resp.put("title", title);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject threadDetails(int thread, boolean rel_user, boolean rel_forum){
        String date = "";
        int dislikes = 0;
        Object forum = null;
        boolean isClosed = false;
        boolean isDeleted = false;
        int likes = 0;
        String message = "";
        int points = 0;
        int posts = 0;
        String slug = "";
        String title = "";
        Object user = null;
        try{
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Threads WHERE thread_id=?");
            stmt.setInt(1, thread);
            ResultSet result = stmt.executeQuery();
            if (!result.isLast()) {
                result.next();
                date = result.getString("created");
                date = date.substring(0, date.length() - 2);
                dislikes = result.getInt("dislikes");
                if (rel_forum) {
                    forum = forumDetails(result.getString("forum_shortname"), false);
                } else {
                    forum = result.getString("forum_shortname");
                }
                isClosed = result.getBoolean("isClosed");
                isDeleted = result.getBoolean("isDeleted");
                likes = result.getInt("likes");
                message = result.getString("message");
                points = likes-dislikes;
                slug = result.getString("slug");
                title = result.getString("title");
                if (rel_user) {
                    user = userDetails(result.getString("user_email"));
                } else {
                    user = result.getString("user_email");
                }
                result.close();
                stmt.close();
                stmt = connection.prepareStatement("SELECT COUNT(*) FROM Posts WHERE thread_id=? AND isDeleted=FALSE");
                stmt.setInt(1, thread);
                result = stmt.executeQuery();
                result.next();
                posts = result.getInt(1);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                String msg = e.getMessage();
                if (msg.equals("Illegal operation on empty result set.")) {
                    return null;
                }
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", date);
        resp.put("dislikes", dislikes);
        resp.put("forum", forum);
        resp.put("id", thread);
        resp.put("isClosed", isClosed);
        resp.put("isDeleted", isDeleted);
        resp.put("likes", likes);
        resp.put("message", message);
        resp.put("points", points);
        resp.put("posts", posts);
        resp.put("slug", slug);
        resp.put("title", title);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject createPost(String forum, Integer thread, Integer parent, Boolean isDeleted, Boolean isApproved,
                                   Boolean isEdited, Boolean isHighlighted, Boolean isSpam, String user, String date, String message){
        int id = 0;
        try{
//            connection.setAutoCommit(false);
            String update = "insert into Posts(thread_id, forum_shortname, user_email, created, message," +
                    "parent, isApproved, isHighlighted, isEdited, isSpam, isDeleted) values(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setInt(1, thread);
            stmt.setString(2, forum);
            stmt.setString(3, user);
            stmt.setString(4, date);
            stmt.setString(5, message);
            stmt.setInt(6, parent);
            stmt.setBoolean(7, isApproved);
            stmt.setBoolean(8, isHighlighted);
            stmt.setBoolean(9, isEdited);
            stmt.setBoolean(10, isSpam);
            stmt.setBoolean(11, isDeleted);
            stmt.executeUpdate();
            stmt.execute("SELECT LAST_INSERT_ID()");
            ResultSet result = stmt.getResultSet();
            result.next();
            id = result.getInt(1);
            result.close();
            stmt.close();
            if (parent.equals(0)) {
                stmt = connection.prepareStatement("insert into ParentPosts(post_id, thread_id, created) values(?,?,?)");
                stmt.setInt(1, id);
                stmt.setInt(2, thread);
                stmt.setString(3, date);
                stmt.executeUpdate();
            } else {
                stmt = connection.prepareStatement("update Posts SET isParent = true WHERE post_id = ?");
                stmt.setInt(1, parent);
                stmt.executeUpdate();
            }
//            connection.commit();
            stmt.close();
//            connection.setAutoCommit(true);
        } catch (SQLException e) {
           /* try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            */
            e.printStackTrace();
        }

        if (id == 0) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", date);
        resp.put("forum", forum);
        resp.put("id", id);
        resp.put("isApproved", isApproved);
        resp.put("isDeleted", isDeleted);
        resp.put("isEdited", isEdited);
        resp.put("isHighlighted", isHighlighted);
        resp.put("isSpam", isSpam);
        resp.put("message", message);
        String sttt = null;
        if (parent.equals(0)) {
            resp.put("parent", sttt);
        } else {
            resp.put("parent", parent);
        }
        resp.put("thread", thread);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject postDetails(Integer post_id, boolean rel_user, boolean rel_forum, boolean rel_thread) {
        String date = "";
        int dislikes = 0;
        Object forum = null;
        boolean isApproved = false;
        boolean isDeleted = false;
        boolean isEdited = false;
        boolean isHighlighted = false;
        boolean isSpam = false;
        int likes = 0;
        String message = "";
        Integer parent = null;
        int points = 0;
        Object thread = null;
        Object user = null;
        try{
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Posts WHERE post_id=?");
            stmt.setInt(1, post_id);
            ResultSet result = stmt.executeQuery();
            if (!result.isLast()) {
                result.next();
                date = result.getString("created");
                date = date.substring(0, date.length() - 2);
                dislikes = result.getInt("dislikes");
                if (rel_forum) {
                    forum = forumDetails(result.getString("forum_shortname"), false);
                } else {
                    forum = result.getString("forum_shortname");
                }
                isApproved = result.getBoolean("isApproved");
                isDeleted = result.getBoolean("isDeleted");
                isEdited = result.getBoolean("isEdited");
                isHighlighted = result.getBoolean("isHighlighted");
                isSpam = result.getBoolean("isSpam");
                likes = result.getInt("likes");
                message = result.getString("message");
                points = likes-dislikes;
                parent = result.getInt("parent");
                if (rel_thread) {
                    thread = threadDetails(result.getInt("thread_id"),false, false);
                } else {
                    thread = result.getInt("thread_id");
                }
                if (rel_user) {
                    user = userDetails(result.getString("user_email"));
                } else {
                    user = result.getString("user_email");
                }
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                String msg = e.getMessage();
                if (msg.equals("Illegal operation on empty result set.")) {
                    return null;
                }
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", date);
        resp.put("dislikes", dislikes);
        resp.put("forum", forum);
        resp.put("id", post_id);
        resp.put("isApproved", isApproved);
        resp.put("isEdited", isEdited);
        resp.put("isHighlighted", isHighlighted);
        resp.put("isSpam", isSpam);
        resp.put("isDeleted", isDeleted);
        resp.put("likes", likes);
        resp.put("message", message);
        if (parent.equals(0))
            parent = null;
        resp.put("parent", parent);
        resp.put("points", points);
        resp.put("thread", thread);
        resp.put("user", user);
        return resp;
    }

    public MyJSONArray postList(String parent_id, boolean thread_set, boolean rel_user, boolean rel_forum, boolean rel_thread, String since, Integer limit, Boolean isAsc){
        int post_id;
        String date;
        int dislikes;
        Object forum;
        boolean isApproved;
        boolean isDeleted;
        boolean isEdited;
        boolean isHighlighted;
        boolean isSpam;
        int likes;
        String message;
        Integer parent;
        int points;
        Object thread;
        Object user;
        StringBuilder query = new StringBuilder("SELECT * FROM Posts WHERE");
        MyJSONArray rez = new MyJSONArray();
        try{
            PreparedStatement stmt;
            if (thread_set) {
                query.append(" thread_id = ?");
            } else {
                query.append(" forum_shortname = ?");
            }
            if (since != null) {
                query.append(" AND created >= ?");
            }
            query.append(" ORDER BY created");
            if (!isAsc) {
                query.append(" DESC");
            }
            if (limit != null) {
                query.append(" LIMIT ?");
            }
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, parent_id);
            if (since != null) {
                stmt.setString(2,since);
            }
            if (limit != null) {
                stmt.setInt(3, limit);
            }
//            System.out.append(stmt.toString());
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                post_id = result.getInt("post_id");
                date = result.getString("created");
                date = date.substring(0, date.length() - 2);
                dislikes = result.getInt("dislikes");
                if (rel_forum) {
                    forum = forumDetails(result.getString("forum_shortname"), false);
                } else {
                    forum = result.getString("forum_shortname");
                }
                isApproved = result.getBoolean("isApproved");
                isDeleted = result.getBoolean("isDeleted");
                isEdited = result.getBoolean("isEdited");
                isHighlighted = result.getBoolean("isHighlighted");
                isSpam = result.getBoolean("isSpam");
                likes = result.getInt("likes");
                message = result.getString("message");
                points = likes-dislikes;
                parent = result.getInt("parent");
                if (rel_thread) {
                    thread = threadDetails(result.getInt("thread_id"),false, false);
                } else {
                    thread = result.getInt("thread_id");
                }
                if (rel_user) {
                    user = userDetails(result.getString("user_email"));
                } else {
                    user = result.getString("user_email");
                }
                MyJSONObject resp = new MyJSONObject();
                resp.put("date", date);
                resp.put("dislikes", dislikes);
                resp.put("forum", forum);
                resp.put("id", post_id);
                resp.put("isApproved", isApproved);
                resp.put("isEdited", isEdited);
                resp.put("isHighlighted", isHighlighted);
                resp.put("isSpam", isSpam);
                resp.put("isDeleted", isDeleted);
                resp.put("likes", likes);
                resp.put("message", message);
                if (parent.equals(0))
                    parent = null;
                resp.put("parent", parent);
                resp.put("points", points);
                resp.put("thread", thread);
                resp.put("user", user);
                rez.put(resp);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                String msg = e.getMessage();
                if (msg.equals("Illegal operation on empty result set.")) {
                    return null;
                }
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return rez;
    }

    public int removePost(Integer post_id) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Posts SET isDeleted=true WHERE post_id=?");
            stmt.setInt(1, post_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            return rez;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    public int restorePost(Integer post_id) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Posts SET isDeleted=false WHERE post_id=?");
            stmt.setInt(1, post_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            return rez;
        } catch (SQLException e) {
            try {
                String msg = e.getMessage();
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    public MyJSONObject updatePost(Integer post_id, String message) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Posts SET message=? WHERE post_id=?");
            stmt.setString(1,message);
            stmt.setInt(2, post_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            if (rez == 0) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postDetails(post_id, false, false, false);
    }

    public MyJSONObject votePost(Integer post_id, Integer vote){
        try{
            String update;
            if (vote.equals(1)) {
                update = "UPDATE Posts SET likes=likes+1 WHERE post_id=?";
            } else {
                update = "UPDATE Posts SET dislikes=dislikes+1 WHERE post_id=?";
            }
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setInt(1, post_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            if (rez == 0) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postDetails(post_id, false, false, false);
    }

    public MyJSONArray threadList(String parent, boolean user_set, boolean rel_user,
                                  boolean rel_forum, String since, Integer limit, Boolean isAsc){
        String date;
        int dislikes;
        Object forum;
        boolean isClosed;
        boolean isDeleted;
        int likes;
        String message;
        int points;
        int posts;
        String slug;
        String title;
        Object user;
        int thread;
        StringBuilder query = new StringBuilder("SELECT * FROM Threads WHERE");
        MyJSONArray rez = new MyJSONArray();
        try{
            PreparedStatement stmt;
            if (user_set) {
                query.append(" user_email = ?");
            } else {
                query.append(" forum_shortname = ?");
            }
            if (since != null) {
                query.append(" AND created >= ?");
            }
            query.append(" ORDER BY created");
            if (!isAsc) {
                query.append(" DESC");
            }
            if (limit != null) {
                query.append(" LIMIT ?");
            }
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, parent);
            if (since != null) {
                stmt.setString(2,since);
            }
            if (limit != null) {
                stmt.setInt(3, limit);
            }
//            System.out.append(stmt.toString());
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                thread = result.getInt("thread_id");
                date = result.getString("created");
                date = date.substring(0, date.length() - 2);
                dislikes = result.getInt("dislikes");
                if (rel_forum) {
                    forum = forumDetails(result.getString("forum_shortname"), false);
                } else {
                    forum = result.getString("forum_shortname");
                }
                isClosed = result.getBoolean("isClosed");
                isDeleted = result.getBoolean("isDeleted");
                likes = result.getInt("likes");
                message = result.getString("message");
                points = likes-dislikes;
                slug = result.getString("slug");
                title = result.getString("title");
                if (rel_user) {
                    user = userDetails(result.getString("user_email"));
                } else {
                    user = result.getString("user_email");
                }
                PreparedStatement stmt1 = connection.prepareStatement("SELECT COUNT(*) FROM Posts WHERE thread_id=? AND isDeleted=FALSE");
                stmt1.setInt(1, thread);
                ResultSet result1 = stmt1.executeQuery();
                result1.next();
                posts = result1.getInt(1);
                result1.close();
                stmt1.close();
                MyJSONObject resp = new MyJSONObject();
                resp.put("date", date);
                resp.put("dislikes", dislikes);
                resp.put("forum", forum);
                resp.put("id", thread);
                resp.put("isClosed", isClosed);
                resp.put("isDeleted", isDeleted);
                resp.put("likes", likes);
                resp.put("message", message);
                resp.put("points", points);
                resp.put("posts", posts);
                resp.put("slug", slug);
                resp.put("title", title);
                resp.put("user", user);
                rez.put(resp);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                String msg = e.getMessage();
                if (msg.equals("Illegal operation on empty result set.")) {
                    return null;
                }
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return rez;
    }

    public MyJSONArray threadListPostsTree(Integer thread_id, String since, Integer limit, Boolean isAsc) {
        System.out.append("НАПИШИ threadListPostsTree!!!\n");
        return null;
    }

    public MyJSONArray threadListPostsParentTree(Integer thread_id, String since, Integer limit, Boolean isAsc) {
        System.out.append("НАПИШИ threadListPostsParentTree!!!\n");
        return null;
    }

    public int removeThread(Integer thread_id) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Threads SET isDeleted=true WHERE thread_id=?");
            stmt.setInt(1, thread_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            stmt = connection.prepareStatement("UPDATE Posts SET isDeleted=true WHERE thread_id=?");
            stmt.setInt(1, thread_id);
            stmt.executeUpdate();
            stmt.close();
            return rez;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int restoreThread(Integer thread_id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE Threads SET isDeleted=false WHERE thread_id=?");
            stmt.setInt(1, thread_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            stmt = connection.prepareStatement("UPDATE Posts SET isDeleted=false WHERE thread_id=?");
            stmt.setInt(1, thread_id);
            stmt.executeUpdate();
            stmt.close();
            return rez;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    public int closeThread(Integer thread_id) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Threads SET isClosed=true WHERE thread_id=?");
            stmt.setInt(1, thread_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            return rez;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int openThread(Integer thread_id) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Threads SET isClosed=false WHERE thread_id=?");
            stmt.setInt(1, thread_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            return rez;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public MyJSONObject updateThread(Integer thread_id, String message, String slug) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Threads SET message=?, slug=? WHERE thread_id=?");
            stmt.setString(1,message);
            stmt.setString(2, slug);
            stmt.setInt(3, thread_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            if (rez == 0) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threadDetails(thread_id, false, false);
    }

    public MyJSONObject voteThread(Integer thread_id, Integer vote){
        try{
            String update;
            if (vote.equals(1)) {
                update = "UPDATE Threads SET likes=likes+1 WHERE thread_id=?";
            } else {
                update = "UPDATE Threads SET dislikes=dislikes+1 WHERE thread_id=?";
            }
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setInt(1, thread_id);
            int rez = stmt.executeUpdate();
            stmt.close();
            if (rez == 0) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threadDetails(thread_id, false, false);
    }

    public void subscribeThread(Integer thread_id, String user_email){
        try{
            String update = "INSERT INTO Subscribers(user_email, thread_id) VALUES(?,?)";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setString(1, user_email);
            stmt.setInt(2, thread_id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribeThread(Integer thread_id, String user_email){
        try{
            String update = " DELETE FROM Subscribers WHERE user_email=? AND thread_id=?;";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setString(1, user_email);
            stmt.setInt(2, thread_id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MyJSONArray userListPosts(String user, String since, Integer limit, Boolean isAsc){
        int post_id;
        String date;
        int dislikes;
        Object forum;
        boolean isApproved;
        boolean isDeleted;
        boolean isEdited;
        boolean isHighlighted;
        boolean isSpam;
        int likes;
        String message;
        Integer parent;
        int points = 0;
        Object thread = null;
        StringBuilder query = new StringBuilder("SELECT * FROM Posts WHERE user_email = ?");
        MyJSONArray rez = new MyJSONArray();
        try{
            PreparedStatement stmt;
            if (since != null) {
                query.append(" AND created >= ?");
            }
            query.append(" ORDER BY created");
            if (!isAsc) {
                query.append(" DESC");
            }
            if (limit != null) {
                query.append(" LIMIT ?");
            }
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, user);
            if (since != null) {
                stmt.setString(2,since);
            }
            if (limit != null) {
                stmt.setInt(3, limit);
            }
//            System.out.append(stmt.toString());
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                post_id = result.getInt("post_id");
                date = result.getString("created");
                date = date.substring(0, date.length() - 2);
                dislikes = result.getInt("dislikes");
                forum = result.getString("forum_shortname");
                isApproved = result.getBoolean("isApproved");
                isDeleted = result.getBoolean("isDeleted");
                isEdited = result.getBoolean("isEdited");
                isHighlighted = result.getBoolean("isHighlighted");
                isSpam = result.getBoolean("isSpam");
                likes = result.getInt("likes");
                message = result.getString("message");
                points = likes-dislikes;
                parent = result.getInt("parent");
                thread = result.getInt("thread_id");
                MyJSONObject resp = new MyJSONObject();
                resp.put("date", date);
                resp.put("dislikes", dislikes);
                resp.put("forum", forum);
                resp.put("id", post_id);
                resp.put("isApproved", isApproved);
                resp.put("isEdited", isEdited);
                resp.put("isHighlighted", isHighlighted);
                resp.put("isSpam", isSpam);
                resp.put("isDeleted", isDeleted);
                resp.put("likes", likes);
                resp.put("message", message);
                if (parent.equals(0))
                    parent = null;
                resp.put("parent", parent);
                resp.put("points", points);
                resp.put("thread", thread);
                resp.put("user", user);
                rez.put(resp);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                String msg = e.getMessage();
                if (msg.equals("Illegal operation on empty result set.")) {
                    return null;
                }
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return rez;
    }

    public MyJSONObject updateUserProfile(String email, String name, String about) {
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Users SET name=?, about=? WHERE user_email=?");
            stmt.setString(1,name);
            stmt.setString(2, about);
            stmt.setString(3, email);
            int rez = stmt.executeUpdate();
            stmt.close();
            if (rez == 0) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDetails(email);
    }

    public MyJSONObject followUser(String follower, String followee){
        try{
            String update = "INSERT INTO Follows(follower, following) VALUES(?,?)";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setString(1, follower);
            stmt.setString(2, followee);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDetails(follower);
    }

    public MyJSONArray userListFollowers(String user, Integer since_id, Integer limit, Boolean isAsc){
        int id;
        String about;
        boolean isAn;
        String name;
        String username;
        ArrayList<String> followers;
        ArrayList<String> following;
        ArrayList<Integer> subscr;
        String email;
        StringBuilder query =
                new StringBuilder("SELECT * FROM Users INNER JOIN Follows ON Users.user_email=Follows.follower WHERE following = ?");
        MyJSONArray rez = new MyJSONArray();
        try{
            PreparedStatement stmt;
            if (since_id != null) {
                query.append(" AND user_id >= ?");
            }
            query.append(" ORDER BY name");
            if (!isAsc) {
                query.append(" DESC");
            }
            if (limit != null) {
                query.append(" LIMIT ?");
            }
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, user);
            if (since_id != null) {
                stmt.setInt(2, since_id);
            }
            if (limit != null) {
                stmt.setInt(3, limit);
            }
//            System.out.append(query);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                id = result.getInt("user_id");
                about = result.getString("about");
                isAn = result.getBoolean("isAnonymous");
                name = result.getString("name");
                username = result.getString("username");
                email = result.getString("user_email");
                PreparedStatement stmt1 = connection.prepareStatement("SELECT follower FROM Follows WHERE following=?");
                stmt1.setString(1, email);
                ResultSet result1 = stmt1.executeQuery();
                followers = new ArrayList<>();
                while (result1.next()){
                    followers.add(result1.getString(1));
                }
                result1.close();
                stmt1.close();
                stmt1 = connection.prepareStatement("SELECT following FROM Follows WHERE follower=?");
                stmt1.setString(1, email);
                result1 = stmt1.executeQuery();
                following = new ArrayList<>();
                while (result1.next()){
                    following.add(result1.getString(1));
                }
                result1.close();
                stmt1.close();
                stmt1 = connection.prepareStatement("SELECT thread_id FROM Subscribers WHERE user_email=?");
                stmt1.setString(1, email);
                result1 = stmt1.executeQuery();
                subscr = new ArrayList<>();
                while (result1.next()){
                    subscr.add(result1.getInt(1));
                }
                result1.close();
                MyJSONObject resp = new MyJSONObject();
                resp.put("about", about);
                resp.put("email", email);
                resp.put("followers", followers);
                resp.put("following", following);
                resp.put("id", id);
                resp.put("isAnonymous", isAn);
                resp.put("name", name);
                resp.put("subscriptions", subscr);
                resp.put("username", username);
                rez.put(resp);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rez;
    }

    public MyJSONArray userListFollowing(String user, Integer since_id, Integer limit, Boolean isAsc){
        int id;
        String about;
        boolean isAn;
        String name;
        String username;
        ArrayList<String> followers;
        ArrayList<String> following;
        ArrayList<Integer> subscr;
        String email;
        StringBuilder query =
                new StringBuilder("SELECT * FROM Users INNER JOIN Follows ON Users.user_email=Follows.following WHERE follower = ?");
        MyJSONArray rez = new MyJSONArray();
        try{
            PreparedStatement stmt;
            if (since_id != null) {
                query.append(" AND user_id >= ?");
            }
            query.append(" ORDER BY name");
            if (!isAsc) {
                query.append(" DESC");
            }
            if (limit != null) {
                query.append(" LIMIT ?");
            }
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, user);
            if (since_id != null) {
                stmt.setInt(2, since_id);
            }
            if (limit != null) {
                stmt.setInt(3, limit);
            }
//            System.out.append(query);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                id = result.getInt("user_id");
                about = result.getString("about");
                isAn = result.getBoolean("isAnonymous");
                name = result.getString("name");
                username = result.getString("username");
                email = result.getString("user_email");
                PreparedStatement stmt1 = connection.prepareStatement("SELECT follower FROM Follows WHERE following=?");
                stmt1.setString(1, email);
                ResultSet result1 = stmt1.executeQuery();
                followers = new ArrayList<>();
                while (result1.next()){
                    followers.add(result1.getString(1));
                }
                result1.close();
                stmt1.close();
                stmt1 = connection.prepareStatement("SELECT following FROM Follows WHERE follower=?");
                stmt1.setString(1, email);
                result1 = stmt1.executeQuery();
                following = new ArrayList<>();
                while (result1.next()){
                    following.add(result1.getString(1));
                }
                result1.close();
                stmt1.close();
                stmt1 = connection.prepareStatement("SELECT thread_id FROM Subscribers WHERE user_email=?");
                stmt1.setString(1, email);
                result1 = stmt1.executeQuery();
                subscr = new ArrayList<>();
                while (result1.next()){
                    subscr.add(result1.getInt(1));
                }
                result1.close();
                MyJSONObject resp = new MyJSONObject();
                resp.put("about", about);
                resp.put("email", email);
                resp.put("followers", followers);
                resp.put("following", following);
                resp.put("id", id);
                resp.put("isAnonymous", isAn);
                resp.put("name", name);
                resp.put("subscriptions", subscr);
                resp.put("username", username);
                rez.put(resp);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rez;
    }

    public MyJSONObject unfollowUser(String follower, String followee){
        try{
            String update = "DELETE FROM Follows WHERE follower=? AND following=?";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setString(1, follower);
            stmt.setString(2, followee);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDetails(follower);
    }

    public MyJSONArray forumListUsers(String forum, Integer since_id, Integer limit, Boolean isAsc){
        int id;
        String about;
        boolean isAn;
        String name;
        String username;
        ArrayList<String> followers;
        ArrayList<String> following;
        ArrayList<Integer> subscr;
        String email;
        StringBuilder query =
                new StringBuilder("SELECT user_id, about, isAnonymous, name, username, Users.user_email FROM " +
                        "Users LEFT JOIN Posts ON Users.user_email=Posts.user_email WHERE forum_shortname = ?");
        MyJSONArray rez = new MyJSONArray();
        try{
            PreparedStatement stmt;
            if (since_id != null) {
                query.append(" AND user_id >= ?");
            }
            query.append(" GROUP BY user_id");
            query.append(" ORDER BY name");
            if (!isAsc) {
                query.append(" DESC");
            }
            if (limit != null) {
                query.append(" LIMIT ?");
            }
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, forum);
            if (since_id != null) {
                stmt.setInt(2, since_id);
            }
            if (limit != null) {
                stmt.setInt(3, limit);
            }
//            System.out.println(stmt.toString());
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                id = result.getInt("user_id");
                about = result.getString("about");
                isAn = result.getBoolean("isAnonymous");
                name = result.getString("name");
                username = result.getString("username");
                email = result.getString("user_email");
                PreparedStatement stmt1 = connection.prepareStatement("SELECT follower FROM Follows WHERE following=?");
                stmt1.setString(1, email);
                ResultSet result1 = stmt1.executeQuery();
                followers = new ArrayList<>();
                while (result1.next()){
                    followers.add(result1.getString(1));
                }
                result1.close();
                stmt1.close();
                stmt1 = connection.prepareStatement("SELECT following FROM Follows WHERE follower=?");
                stmt1.setString(1, email);
                result1 = stmt1.executeQuery();
                following = new ArrayList<>();
                while (result1.next()){
                    following.add(result1.getString(1));
                }
                result1.close();
                stmt1.close();
                stmt1 = connection.prepareStatement("SELECT thread_id FROM Subscribers WHERE user_email=?");
                stmt1.setString(1, email);
                result1 = stmt1.executeQuery();
                subscr = new ArrayList<>();
                while (result1.next()){
                    subscr.add(result1.getInt(1));
                }
                result1.close();
                MyJSONObject resp = new MyJSONObject();
                resp.put("about", about);
                resp.put("email", email);
                resp.put("followers", followers);
                resp.put("following", following);
                resp.put("id", id);
                resp.put("isAnonymous", isAn);
                resp.put("name", name);
                resp.put("subscriptions", subscr);
                resp.put("username", username);
                rez.put(resp);
            }
            result.close();
            stmt.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rez;
    }

}
