package base;

import dataSets.ForumDataSet;
import dataSets.PostDataSet;
import dataSets.ThreadDataSet;
import dataSets.UserDataSet;
import executor.PreparedTExecutor;
import handlers.TResultHandler;
import utils.MyJSONArray;
import utils.MyJSONObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by serg on 13.09.14.
 */
public class DBServiceImpl implements DatabaseService {

    private DataSource ds;

    private PreparedTExecutor executor;

    public DBServiceImpl() {
        try {
            InitialContext initContext = new InitialContext();
            ds = (DataSource) initContext.lookup("java:comp/env/jdbc/FORUMS_TP");
            executor = new PreparedTExecutor();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String clear(){
        try(Connection connection = ds.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("Truncate Forums");
                stmt.execute("Truncate Users");
                stmt.execute("Truncate Threads");
                stmt.execute("Truncate Posts");
                stmt.execute("Truncate ParentPosts");
                stmt.execute("Truncate Subscribers");
                stmt.execute("Truncate Follows");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    public MyJSONObject createUser(String username ,String about, String name, String email, Boolean isAn){
        int id = -1;
        String update = "INSERT into Users(user_email, username, name, about, isAnonymous) values(?,?,?,?,?)";
        int n;
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(email);
            n = executor.execQuery(connection, "SELECT Count(*) FROM Users WHERE user_email=?", params, new TResultHandler<Integer>(){
                public Integer handle(ResultSet result) throws SQLException {
                    if (result.next()) {
                        return result.getInt(1);
                    } else {
                        return -1;
                    }
                }
            });
            if (n == 0) {
                params.add(username);
                params.add(name);
                params.add(about);
                params.add(isAn);
                id = executor.execInsert(connection, update, params);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (id == -1) {
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

    public MyJSONObject userDetails(String email) {
        UserDataSet user = null;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(email);
            user = executor.execQuery(connection, "SELECT * FROM Users WHERE user_email=?", params, result -> {
                if (result.next()) {
                    return new UserDataSet(result.getInt("user_id"), result.getString("username"),
                            result.getString("user_email"), result.getString("about"),
                            result.getBoolean("isAnonymous"), result.getString("name"));
                } else return null;
            });
            if (user != null) {
                ArrayList<String> followers = executor.execQuery(connection, "SELECT follower FROM Follows WHERE following=?",
                        params, result -> {
                            ArrayList<String> followers1 = new ArrayList<>();
                            while (result.next()) {
                                followers1.add(result.getString(1));
                            }
                            return followers1;
                        }
                );
                user.SetFollowers(followers);
                ArrayList<String> following = executor.execQuery(connection, "SELECT following FROM Follows WHERE follower=?",
                        params, result -> {
                            ArrayList<String> following1 = new ArrayList<>();
                            while (result.next()) {
                                following1.add(result.getString(1));
                            }
                            return following1;
                        }
                );
                user.SetFollowings(following);
                ArrayList<Integer> subscr = executor.execQuery(connection, "SELECT thread_id FROM Subscribers WHERE user_email=?",
                        params, result -> {
                            ArrayList<Integer> subscr1 = new ArrayList<>();
                            while (result.next()) {
                                subscr1.add(result.getInt(1));
                            }
                            return subscr1;
                        }
                );
                user.SetSubscr(subscr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user == null) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("about", user.GetAbout());
        resp.put("email", user.GetEmail());
        resp.put("followers", user.GetFollowers());
        resp.put("following", user.GetFollowing());
        resp.put("id", user.GetId());
        resp.put("isAnonymous", user.GetIsAn());
        resp.put("name", user.GetName());
        resp.put("subscriptions", user.GetSubscr());
        resp.put("username", user.GetUsername());
        return resp;
    }

    public MyJSONObject userDetails2(String email, Connection connection) throws SQLException {
        UserDataSet user;
        ArrayList<Object> params = new ArrayList<>();
        params.add(email);
        user = executor.execQuery(connection, "SELECT * FROM Users WHERE user_email=?", params, result -> {
            if (result.next()) {
                return new UserDataSet(result.getInt("user_id"), result.getString("username"),
                        result.getString("user_email"), result.getString("about"),
                        result.getBoolean("isAnonymous"), result.getString("name"));
            } else return null;
        });
        if (user != null) {
            ArrayList<String> followers = executor.execQuery(connection, "SELECT follower FROM Follows WHERE following=?",
                    params, result -> {
                        ArrayList<String> followers1 = new ArrayList<>();
                        while (result.next()) {
                            followers1.add(result.getString(1));
                        }
                        return followers1;
                    }
            );
            user.SetFollowers(followers);
            ArrayList<String> following = executor.execQuery(connection, "SELECT following FROM Follows WHERE follower=?",
                    params, result -> {
                        ArrayList<String> following1 = new ArrayList<>();
                        while (result.next()) {
                            following1.add(result.getString(1));
                        }
                        return following1;
                    }
            );
            user.SetFollowings(following);
            ArrayList<Integer> subscr = executor.execQuery(connection, "SELECT thread_id FROM Subscribers WHERE user_email=?",
                    params,  result -> {
                        ArrayList<Integer> subscr1 = new ArrayList<>();
                        while (result.next()) {
                            subscr1.add(result.getInt(1));
                        }
                        return subscr1;
                    }
            );
            user.SetSubscr(subscr);
        }
        if (user == null) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("about", user.GetAbout());
        resp.put("email", user.GetEmail());
        resp.put("followers", user.GetFollowers());
        resp.put("following", user.GetFollowing());
        resp.put("id", user.GetId());
        resp.put("isAnonymous", user.GetIsAn());
        resp.put("name", user.GetName());
        resp.put("subscriptions", user.GetSubscr());
        resp.put("username", user.GetUsername());
        return resp;
    }

    public MyJSONObject createForum(String name, String short_name, String user){
        int id = -1;
        int cnt;
        String update = "INSERT into Forums(forum_shortname, forum_name, user_email) values(?,?,?)";
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(short_name);
            cnt = executor.execQuery(connection, "SELECT COUNT(*) FROM Forums WHERE forum_shortname=?", params, result -> {
                if (result.next()) {
                    return result.getInt(1);
                } else {
                    return -1;
                }
            });
            if (cnt == 0) {
                params.add(name);
                params.add(user);
                id = executor.execInsert(connection, update, params);
            } else if (cnt != -1) {
                return forumDetails2(short_name, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (id == -1) {
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
        Object user = null;
        ForumDataSet forum = null;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(short_name);
            forum = executor.execQuery(connection, "SELECT * FROM Forums WHERE forum_shortname=?", params, result -> {
                if (result.next()) {
                    return new ForumDataSet(result.getInt("forum_id"), result.getString("forum_shortname"),
                            result.getString("forum_name"), result.getString("user_email"));
                } else return null;
            });
            if (forum != null) {
                if (rel_user) {
                    user = userDetails2(forum.GetUser(), connection);
                } else {
                    user = forum.GetUser();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (forum == null) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("id", forum.GetId());
        resp.put("name", forum.GetName());
        resp.put("short_name", forum.GetShort_name());
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject forumDetails2 (String short_name, Connection connection) throws SQLException {
        ForumDataSet forum;
            ArrayList<Object> params = new ArrayList<>();
            params.add(short_name);
            forum = executor.execQuery(connection, "SELECT * FROM Forums WHERE forum_shortname=?", params, result -> {
                if (result.next()) {
                    return new ForumDataSet(result.getInt("forum_id"), result.getString("forum_shortname"),
                            result.getString("forum_name"), result.getString("user_email"));
                } else return null;
            });
        if (forum == null) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("id", forum.GetId());
        resp.put("name", forum.GetName());
        resp.put("short_name", forum.GetShort_name());
        resp.put("user", forum.GetUser());
        return resp;
    }

    public MyJSONObject createThread(String forum, String title, Boolean isClosed, Boolean isDeleted,
                                     String user, String date, String message, String slug) {
        int id = -1;
        String update = "insert into Threads(created, message, slug, title, isClosed, isDeleted, forum_shortname, user_email) values(?,?,?,?,?,?,?,?)";
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(date);
            params.add(message);
            params.add(slug);
            params.add(title);
            params.add(isClosed);
            params.add(isDeleted);
            params.add(forum);
            params.add(user);
            id = executor.execInsert(connection, update, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (id == -1) {
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

    public MyJSONObject threadDetails(int threadId, boolean rel_user, boolean rel_forum) {
        ThreadDataSet thread = null;
        int posts = 0;
        Object forum = null;
        Object user = null;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(threadId);
            thread = executor.execQuery(connection, "SELECT * FROM Threads WHERE thread_id=?", params, result -> {
                if (result.next()) {
                    return new ThreadDataSet(result.getInt("thread_id") ,result.getString("created"), result.getInt("dislikes"),
                            result.getString("forum_shortname"), result.getBoolean("isClosed"),
                            result.getBoolean("isDeleted"), result.getInt("likes"),
                            result.getString("message"), result.getString("slug"),
                            result.getString("title"), result.getString("user_email"));
                } else return null;
            });
            if (thread != null) {
                if (rel_forum) {
                    forum = forumDetails2(thread.GetForum(), connection);
                } else {
                    forum = thread.GetForum();
                }
                if (rel_user) {
                    user = userDetails2(thread.GetUser(), connection);
                } else {
                    user = thread.GetUser();
                }
                posts = executor.execQuery(connection, "SELECT COUNT(*) FROM Posts WHERE thread_id=? AND isDeleted=FALSE", params, result -> {
                    if (result.next()) {
                        return result.getInt(1);
                    } else {
                        return -1;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (thread == null ) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", thread.GetDate());
        resp.put("dislikes", thread.GetDislikes());
        resp.put("forum", forum);
        resp.put("id", threadId);
        resp.put("isClosed", thread.GetIsClosed());
        resp.put("isDeleted", thread.GetIsDeleted());
        resp.put("likes", thread.GetLikes());
        resp.put("message", thread.GetMessage());
        resp.put("points", thread.GetPoints());
        resp.put("posts", posts);
        resp.put("slug", thread.GetSlug());
        resp.put("title", thread.GetTitle());
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject threadDetails2(int threadId, Connection connection) throws SQLException {
        ThreadDataSet thread;
        int posts;
        ArrayList<Object> params = new ArrayList<>();
        params.add(threadId);
        thread = executor.execQuery(connection, "SELECT * FROM Threads WHERE thread_id=?", params, result -> {
            if (result.next()) {
                return new ThreadDataSet(result.getInt("thread_id") ,result.getString("created"), result.getInt("dislikes"),
                        result.getString("forum_shortname"), result.getBoolean("isClosed"),
                        result.getBoolean("isDeleted"), result.getInt("likes"),
                        result.getString("message"), result.getString("slug"),
                        result.getString("title"), result.getString("user_email"));
            } else return null;
        });
        if (thread != null) {
            posts = executor.execQuery(connection, "SELECT COUNT(*) FROM Posts WHERE thread_id=? AND isDeleted=FALSE", params, result -> {
                if (result.next()) {
                    return result.getInt(1);
                } else {
                    return -1;
                }
            });
        } else {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", thread.GetDate());
        resp.put("dislikes", thread.GetDislikes());
        resp.put("forum", thread.GetForum());
        resp.put("id", threadId);
        resp.put("isClosed", thread.GetIsClosed());
        resp.put("isDeleted", thread.GetIsDeleted());
        resp.put("likes", thread.GetLikes());
        resp.put("message", thread.GetMessage());
        resp.put("points", thread.GetPoints());
        resp.put("posts", posts);
        resp.put("slug", thread.GetSlug());
        resp.put("title", thread.GetTitle());
        resp.put("user", thread.GetUser());
        return resp;
    }

    public MyJSONObject createPost(String forum, Integer thread, Integer parent, Boolean isDeleted, Boolean isApproved,
                                   Boolean isEdited, Boolean isHighlighted, Boolean isSpam, String user, String date, String message){
        int id = -1;
        String update = "insert into Posts(thread_id, forum_shortname, user_email, created, message," +
                    "parent, isApproved, isHighlighted, isEdited, isSpam, isDeleted) values(?,?,?,?,?,?,?,?,?,?,?)";
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(thread);
            params.add(forum);
            params.add(user);
            params.add(date);
            params.add(message);
            params.add(parent);
            params.add(isApproved);
            params.add(isHighlighted);
            params.add(isEdited);
            params.add(isSpam);
            params.add(isDeleted);
            id = executor.execInsert(connection, update, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
/*
 *       if (parent.equals(0)) {
 *           try(PreparedStatement stmt = connection.prepareStatement("insert into ParentPosts(post_id, thread_id, created) values(?,?,?)")) {
 *               stmt.setInt(1, id);
 *               stmt.setInt(2, thread);
 *               stmt.setString(3, date);
 *               stmt.executeUpdate();
 *           } catch (SQLException e) {
 *               e.printStackTrace();
 *           }
 *       } else {
 *           try(PreparedStatement stmt = connection.prepareStatement("update Posts SET isParent = true WHERE post_id = ?")) {
 *               stmt.setInt(1, parent);
 *               stmt.executeUpdate();
 *           } catch (SQLException e) {
 *               e.printStackTrace();
 *           }
 *       }
 */
        if (id == -1) {
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
        if (parent.equals(0)) {
            resp.put("parent", (String)null);
        } else {
            resp.put("parent", parent);
        }
        resp.put("thread", thread);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject postDetails(Integer post_id, boolean rel_user, boolean rel_forum, boolean rel_thread) {
        PostDataSet post = null;
        Object forum = null;
        Object thread = null;
        Object user = null;
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(post_id);
            post = executor.execQuery(connection, "SELECT * FROM Posts WHERE post_id=?", params, result -> {
                if (result.next()) {
                    return new PostDataSet(result.getInt("post_id") ,result.getString("created"), result.getInt("dislikes"),
                            result.getString("forum_shortname"), result.getBoolean("isApproved"),
                            result.getBoolean("isDeleted"), result.getBoolean("isEdited"),
                            result.getBoolean("isHighlighted"), result.getBoolean("isSpam"),
                            result.getInt("likes"), result.getString("message"), result.getInt("parent"),
                            result.getInt("thread_id"), result.getString("user_email"));
                } else return null;
            });
            if (post != null) {
                if (rel_forum) {
                    forum = forumDetails2(post.GetForum(), connection);
                } else {
                    forum = post.GetForum();
                }
                if (rel_thread) {
                    thread = threadDetails2(post.GetThread(), connection);
                } else {
                    thread = post.GetThread();
                }
                if (rel_user) {
                    user = userDetails2(post.GetUser(), connection);
                } else {
                    user = post.GetUser();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (post == null) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", post.GetDate());
        resp.put("dislikes", post.GetDislikes());
        resp.put("forum", forum);
        resp.put("id", post_id);
        resp.put("isApproved", post.GetIsApproved());
        resp.put("isEdited", post.GetIsEdited());
        resp.put("isHighlighted", post.GetIsHighlighted());
        resp.put("isSpam", post.GetIsSpam());
        resp.put("isDeleted", post.GetIsDeleted());
        resp.put("likes", post.GetLikes());
        resp.put("message", post.GetMessage());
        if (post.GetParent().equals(0)) {
            resp.put("parent", (Integer)null);
        } else {
            resp.put("parent", post.GetParent());
        }
        resp.put("points", post.GetPoints());
        resp.put("thread", thread);
        resp.put("user", user);
        return resp;
    }

    public MyJSONObject postDetails2(Integer post_id, Connection connection) throws SQLException {
        PostDataSet post;
        ArrayList<Object> params = new ArrayList<>();
        params.add(post_id);
        post = executor.execQuery(connection, "SELECT * FROM Posts WHERE post_id=?", params, result -> {
            if (result.next()) {
                return new PostDataSet(result.getInt("post_id") ,result.getString("created"), result.getInt("dislikes"),
                        result.getString("forum_shortname"), result.getBoolean("isApproved"),
                        result.getBoolean("isDeleted"), result.getBoolean("isEdited"),
                        result.getBoolean("isHighlighted"), result.getBoolean("isSpam"),
                        result.getInt("likes"), result.getString("message"), result.getInt("parent"),
                        result.getInt("thread_id"), result.getString("user_email"));
            } else return null;
        });
        if (post == null) {
            return null;
        }
        MyJSONObject resp = new MyJSONObject();
        resp.put("date", post.GetDate());
        resp.put("dislikes", post.GetDislikes());
        resp.put("forum", post.GetForum());
        resp.put("id", post_id);
        resp.put("isApproved", post.GetIsApproved());
        resp.put("isEdited", post.GetIsEdited());
        resp.put("isHighlighted", post.GetIsHighlighted());
        resp.put("isSpam", post.GetIsSpam());
        resp.put("isDeleted", post.GetIsDeleted());
        resp.put("likes", post.GetLikes());
        resp.put("message", post.GetMessage());
        if (post.GetParent().equals(0)) {
            resp.put("parent", (Integer)null);
        } else {
            resp.put("parent", post.GetParent());
        }
        resp.put("points", post.GetPoints());
        resp.put("thread", post.GetThread());
        resp.put("user", post.GetUser());
        return resp;
    }

    public MyJSONArray postList(String parent_id, boolean thread_set, boolean rel_user, boolean rel_forum,
                                boolean rel_thread, String since, Integer limit, Boolean isAsc){
        Object forum;
        Object thread;
        Object user;
        ArrayList<PostDataSet> posts;
        ArrayList<Object> params = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Posts WHERE");
        params.add(parent_id);
        if (thread_set) {
            query.append(" thread_id = ?");
        } else {
            query.append(" forum_shortname = ?");
        }
        if (since != null) {
            query.append(" AND created >= ?");
            params.add(since);
        }
        query.append(" ORDER BY created");
        if (!isAsc) {
            query.append(" DESC");
        }
        if (limit != null) {
            query.append(" LIMIT ?");
            params.add(limit);
        }
        MyJSONArray rez = new MyJSONArray();
        try(Connection connection = ds.getConnection()) {
            posts = executor.execQuery(connection, query.toString(), params, result -> {
                ArrayList<PostDataSet> posts1 = new ArrayList<>();
                while (result.next()) {
                    posts1.add(new PostDataSet(result.getInt("post_id"), result.getString("created"), result.getInt("dislikes"),
                            result.getString("forum_shortname"), result.getBoolean("isApproved"),
                            result.getBoolean("isDeleted"), result.getBoolean("isEdited"),
                            result.getBoolean("isHighlighted"), result.getBoolean("isSpam"),
                            result.getInt("likes"), result.getString("message"), result.getInt("parent"),
                            result.getInt("thread_id"), result.getString("user_email")));
                }
                return posts1;
            });
            for (PostDataSet post : posts) {
                if (rel_forum) {
                    forum = forumDetails2(post.GetForum(), connection);
                } else {
                    forum = post.GetForum();
                }
                if (rel_thread) {
                    thread = threadDetails2(post.GetThread(), connection);
                } else {
                    thread = post.GetThread();
                }
                if (rel_user) {
                    user = userDetails2(post.GetUser(), connection);
                } else {
                    user = post.GetUser();
                }
                MyJSONObject resp = new MyJSONObject();
                resp.put("date", post.GetDate());
                resp.put("dislikes", post.GetDislikes());
                resp.put("forum", forum);
                resp.put("id", post.GetId());
                resp.put("isApproved", post.GetIsApproved());
                resp.put("isEdited", post.GetIsEdited());
                resp.put("isHighlighted", post.GetIsHighlighted());
                resp.put("isSpam", post.GetIsSpam());
                resp.put("isDeleted", post.GetIsDeleted());
                resp.put("likes", post.GetLikes());
                resp.put("message", post.GetMessage());
                if (post.GetParent().equals(0)) {
                    resp.put("parent", (Integer)null);
                } else {
                    resp.put("parent", post.GetParent());
                }
                resp.put("points", post.GetPoints());
                resp.put("thread", thread);
                resp.put("user", user);
                rez.put(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rez;
    }

    public int removePost(Integer post_id) {
        int rez = 0;
        try(Connection connection = ds.getConnection()) {
            ArrayList<Integer> params = new ArrayList<>();
            params.add(post_id);
            rez = executor.execUpdate(connection, "UPDATE Posts SET isDeleted=true WHERE post_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public int restorePost(Integer post_id) {
        int rez = 0;
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(post_id);
            rez = executor.execUpdate(connection, "UPDATE Posts SET isDeleted=false WHERE post_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public MyJSONObject updatePost(Integer post_id, String message) {
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>();
            params.add(message);
            params.add(post_id);
            int rez = executor.execUpdate(connection, "UPDATE Posts SET message=? WHERE post_id=?", params);
            if (rez == 0) {
                return null;
            } else {
                return postDetails2(post_id, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSONObject votePost(Integer post_id, Integer vote){
        String update;
        if (vote.equals(1)) {
            update = "UPDATE Posts SET likes=likes+1 WHERE post_id=?";
        } else {
            update = "UPDATE Posts SET dislikes=dislikes+1 WHERE post_id=?";
        }
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>(1);
            params.add(post_id);
            int rez = executor.execUpdate(connection, update, params);
            if (rez == 0) {
                return null;
            } else {
                return postDetails2(post_id, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSONArray threadList(String parent, boolean user_set, boolean rel_user,
                                  boolean rel_forum, String since, Integer limit, Boolean isAsc){
        ArrayList<ThreadDataSet> threads;
        Object forum;
        Object user;
        int posts;
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Integer> params1 = new ArrayList<>(1);
        params1.add(1);
        StringBuilder query = new StringBuilder("SELECT * FROM Threads WHERE");
        params.add(parent);
        if (user_set) {
            query.append(" user_email = ?");
        } else {
            query.append(" forum_shortname = ?");
        }
        if (since != null) {
            params.add(since);
            query.append(" AND created >= ?");
        }
        query.append(" ORDER BY created");
        if (!isAsc) {
            query.append(" DESC");
        }
        if (limit != null) {
            params.add(limit);
            query.append(" LIMIT ?");
        }
        MyJSONArray rez = new MyJSONArray();
        try (Connection connection = ds.getConnection()) {
            threads = executor.execQuery(connection, query.toString(), params, result -> {
                ArrayList<ThreadDataSet> threads1 = new ArrayList<>();
                while (result.next()) {
                    threads1.add(new ThreadDataSet(result.getInt("thread_id"), result.getString("created"), result.getInt("dislikes"),
                            result.getString("forum_shortname"), result.getBoolean("isClosed"),
                            result.getBoolean("isDeleted"), result.getInt("likes"),
                            result.getString("message"), result.getString("slug"),
                            result.getString("title"), result.getString("user_email")));
                }
                return threads1;
            });
            for (ThreadDataSet thread : threads) {
                if (rel_forum) {
                    forum = forumDetails2(thread.GetForum(), connection);
                } else {
                    forum = thread.GetForum();
                }
                if (rel_user) {
                    user = userDetails2(thread.GetUser(), connection);
                } else {
                    user = thread.GetUser();
                }
                params1.set(0, thread.GetId());
                posts = executor.execQuery(connection,
                        "SELECT COUNT(*) FROM Posts WHERE thread_id=? AND isDeleted=FALSE", params1,
                        result -> {
                            if (result.next()) {
                                return result.getInt(1);
                            } else return 0;
                        }
                );
                MyJSONObject resp = new MyJSONObject();
                resp.put("date", thread.GetDate());
                resp.put("dislikes", thread.GetDislikes());
                resp.put("forum", forum);
                resp.put("id", thread.GetId());
                resp.put("isClosed", thread.GetIsClosed());
                resp.put("isDeleted", thread.GetIsDeleted());
                resp.put("likes", thread.GetLikes());
                resp.put("message", thread.GetMessage());
                resp.put("points", thread.GetPoints());
                resp.put("posts", posts);
                resp.put("slug", thread.GetSlug());
                resp.put("title", thread.GetTitle());
                resp.put("user", user);
                rez.put(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public MyJSONArray threadListPostsTree(Integer thread_id, String since, Integer limit, Boolean isAsc) {
        System.out.append("WRITE threadListPostsTree!!!\n");
        return null;
    }

    public MyJSONArray threadListPostsParentTree(Integer thread_id, String since, Integer limit, Boolean isAsc) {
        System.out.append("WRITE threadListPostsParentTree!!!\n");
        return null;
    }

    public int removeThread(Integer thread_id) {
        int rez = 0;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Integer> params = new ArrayList<>(1);
            params.add(thread_id);
            rez = executor.execUpdate(connection, "UPDATE Threads SET isDeleted=true WHERE thread_id=?", params);
            executor.execUpdate(connection, "UPDATE Posts SET isDeleted=true WHERE thread_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public int restoreThread(Integer thread_id) {
        int rez = 0;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Integer> params = new ArrayList<>(1);
            params.add(thread_id);
            rez = executor.execUpdate(connection, "UPDATE Threads SET isDeleted=false WHERE thread_id=?", params);
            executor.execUpdate(connection, "UPDATE Posts SET isDeleted=false WHERE thread_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public int closeThread(Integer thread_id) {
        int rez = 0;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Integer> params = new ArrayList<>(1);
            params.add(thread_id);
            rez = executor.execUpdate(connection, "UPDATE Threads SET isClosed=true WHERE thread_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public int openThread(Integer thread_id) {
        int rez = 0;
        try (Connection connection = ds.getConnection()) {
            ArrayList<Integer> params = new ArrayList<>(1);
            params.add(thread_id);
            rez = executor.execUpdate(connection, "UPDATE Threads SET isClosed=false WHERE thread_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public MyJSONObject updateThread(Integer thread_id, String message, String slug) {
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>(3);
            params.add(message);
            params.add(slug);
            params.add(thread_id);
            int rez = executor.execUpdate(connection, "UPDATE Threads SET message=?, slug=? WHERE thread_id=?", params);
            if (rez == 0) {
                return null;
            } else {
                return threadDetails2(thread_id, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSONObject voteThread(Integer thread_id, Integer vote){
        String update;
        if (vote.equals(1)) {
            update = "UPDATE Threads SET likes=likes+1 WHERE thread_id=?";
        } else {
            update = "UPDATE Threads SET dislikes=dislikes+1 WHERE thread_id=?";
        }
        try(Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>(1);
            params.add(thread_id);
            int rez = executor.execUpdate(connection, update, params);
            if (rez == 0) {
                return null;
            } else {
                return threadDetails2(thread_id, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void subscribeThread(Integer thread_id, String user_email){
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>(2);
            params.add(user_email);
            params.add(thread_id);
            executor.execUpdate(connection, "INSERT INTO Subscribers(user_email, thread_id) VALUES(?,?)", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribeThread(Integer thread_id, String user_email){
        try (Connection connection = ds.getConnection()) {
            ArrayList<Object> params = new ArrayList<>(2);
            params.add(user_email);
            params.add(thread_id);
            executor.execUpdate(connection, "DELETE FROM Subscribers WHERE user_email=? AND thread_id=?", params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MyJSONArray userListPosts(String user, String since, Integer limit, Boolean isAsc){
        ArrayList<PostDataSet> posts;
        ArrayList<Object> params = new ArrayList<>(3);
        params.add(user);
        StringBuilder query = new StringBuilder("SELECT * FROM Posts WHERE user_email = ?");
        if (since != null) {
            params.add(since);
            query.append(" AND created >= ?");
        }
        query.append(" ORDER BY created");
        if (!isAsc) {
            query.append(" DESC");
        }
        if (limit != null) {
            params.add(limit);
            query.append(" LIMIT ?");
        }
        MyJSONArray rez = new MyJSONArray();
        try(Connection connection = ds.getConnection()) {
            posts = executor.execQuery(connection, query.toString(), params, result -> {
                int i = 0;
                while (result.next()) i++;
                result.beforeFirst();
                ArrayList<PostDataSet> posts1 = new ArrayList<>(i);
                while (result.next()) {
                    posts1.add(new PostDataSet(result.getInt("post_id") ,result.getString("created"), result.getInt("dislikes"),
                            result.getString("forum_shortname"), result.getBoolean("isApproved"),
                            result.getBoolean("isDeleted"), result.getBoolean("isEdited"),
                            result.getBoolean("isHighlighted"), result.getBoolean("isSpam"),
                            result.getInt("likes"), result.getString("message"), result.getInt("parent"),
                            result.getInt("thread_id"), result.getString("user_email")));
                }
                return posts1;
            });
                for (PostDataSet post : posts) {
                MyJSONObject resp = new MyJSONObject();
                resp.put("date", post.GetDate());
                resp.put("dislikes", post.GetDislikes());
                resp.put("forum", post.GetForum());
                resp.put("id", post.GetId());
                resp.put("isApproved", post.GetIsApproved());
                resp.put("isEdited", post.GetIsEdited());
                resp.put("isHighlighted", post.GetIsHighlighted());
                resp.put("isSpam", post.GetIsSpam());
                resp.put("isDeleted", post.GetIsDeleted());
                resp.put("likes", post.GetLikes());
                resp.put("message", post.GetMessage());
                if (post.GetParent().equals(0)) {
                    resp.put("parent", (Integer)null);
                } else {
                    resp.put("parent", post.GetParent());
                }
                resp.put("points", post.GetPoints());
                resp.put("thread", post.GetThread());
                resp.put("user", user);
                rez.put(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public MyJSONObject updateUserProfile(String email, String name, String about) {
        try(Connection connection = ds.getConnection()) {
            ArrayList<String> params = new ArrayList<>(3);
            params.add(name);
            params.add(about);
            params.add(email);
            int rez = executor.execUpdate(connection, "UPDATE Users SET name=?, about=? WHERE user_email=?", params);
            if (rez == 0) {
                return null;
            } else return userDetails2(email, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSONObject followUser(String follower, String followee){
        try(Connection connection = ds.getConnection()) {
            ArrayList<String> params = new ArrayList<>(2);
            params.add(follower);
            params.add(followee);
            executor.execUpdate(connection, "INSERT INTO Follows(follower, following) VALUES(?,?)", params);
            return userDetails2(follower, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSONArray userListFollowers(String user_email, Integer since_id, Integer limit, Boolean isAsc){
        ArrayList<UserDataSet> users;
        ArrayList<String> params1 = new ArrayList<>(1);
        params1.add("");
        ArrayList<Object> params = new ArrayList<>(3);
        params.add(user_email);
        StringBuilder query =
                new StringBuilder("SELECT * FROM Users INNER JOIN Follows ON Users.user_email=Follows.follower WHERE following = ?");
        if (since_id != null) {
            params.add(since_id);
            query.append(" AND user_id >= ?");
        }
        query.append(" ORDER BY name");
        if (!isAsc) {
            query.append(" DESC");
        }
        if (limit != null) {
            params.add(limit);
            query.append(" LIMIT ?");
        }
        MyJSONArray rez = new MyJSONArray();
        try (Connection connection = ds.getConnection()) {
            users = executor.execQuery(connection, query.toString(), params, result -> {
                int i = 0;
                while (result.next()) i++;
                result.beforeFirst();
                ArrayList<UserDataSet> users1 = new ArrayList<>(i);
                while (result.next()) {
                    users1.add(new UserDataSet(result.getInt("user_id"), result.getString("username"),
                            result.getString("user_email"), result.getString("about"),
                            result.getBoolean("isAnonymous"), result.getString("name")));
                }
                return users1;
            });
            for (UserDataSet user: users) {
                params1.set(0, user.GetEmail());
                ArrayList<String> followers = executor.execQuery(connection, "SELECT follower FROM Follows WHERE following=?",
                        params1, result -> {
                            ArrayList<String> followers1 = new ArrayList<>();
                            while (result.next()) {
                                followers1.add(result.getString(1));
                            }
                            return followers1;
                        }
                );
                ArrayList<String> following = executor.execQuery(connection, "SELECT following FROM Follows WHERE follower=?",
                        params1, result -> {
                            ArrayList<String> following1 = new ArrayList<>();
                            while (result.next()) {
                                following1.add(result.getString(1));
                            }
                            return following1;
                        }
                );
                ArrayList<Integer> subscr = executor.execQuery(connection, "SELECT thread_id FROM Subscribers WHERE user_email=?",
                        params1, result -> {
                            ArrayList<Integer> subscr1 = new ArrayList<>();
                            while (result.next()) {
                                subscr1.add(result.getInt(1));
                            }
                            return subscr1;
                        }
                );
                MyJSONObject resp = new MyJSONObject();
                resp.put("about", user.GetAbout());
                resp.put("email", user.GetEmail());
                resp.put("followers", followers);
                resp.put("following", following);
                resp.put("id", user.GetId());
                resp.put("isAnonymous", user.GetIsAn());
                resp.put("name", user.GetName());
                resp.put("subscriptions", subscr);
                resp.put("username", user.GetUsername());
                rez.put(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public MyJSONArray userListFollowing(String user_email, Integer since_id, Integer limit, Boolean isAsc){
        ArrayList<UserDataSet> users;
        ArrayList<String> params1 = new ArrayList<>(1);
        params1.add("");
        ArrayList<Object> params = new ArrayList<>(3);
        params.add(user_email);
        StringBuilder query =
                new StringBuilder("SELECT * FROM Users INNER JOIN Follows ON Users.user_email=Follows.following WHERE follower = ?");
        if (since_id != null) {
            params.add(since_id);
            query.append(" AND user_id >= ?");
        }
        query.append(" ORDER BY name");
        if (!isAsc) {
            query.append(" DESC");
        }
        if (limit != null) {
            params.add(limit);
            query.append(" LIMIT ?");
        }
        MyJSONArray rez = new MyJSONArray();
        try (Connection connection = ds.getConnection()) {
            users = executor.execQuery(connection, query.toString(), params, result -> {
                int i = 0;
                while (result.next()) i++;
                result.beforeFirst();
                ArrayList<UserDataSet> users1 = new ArrayList<>(i);
                while (result.next()) {
                    users1.add(new UserDataSet(result.getInt("user_id"), result.getString("username"),
                            result.getString("user_email"), result.getString("about"),
                            result.getBoolean("isAnonymous"), result.getString("name")));
                }
                return users1;
            });
            for (UserDataSet user: users) {
                params1.set(0, user.GetEmail());
                ArrayList<String> followers = executor.execQuery(connection, "SELECT follower FROM Follows WHERE following=?",
                        params1, result -> {
                            ArrayList<String> followers1 = new ArrayList<>();
                            while (result.next()) {
                                followers1.add(result.getString(1));
                            }
                            return followers1;
                        }
                );
                ArrayList<String> following = executor.execQuery(connection, "SELECT following FROM Follows WHERE follower=?",
                        params1, result -> {
                            ArrayList<String> following1 = new ArrayList<>();
                            while (result.next()) {
                                following1.add(result.getString(1));
                            }
                            return following1;
                        }
                );
                ArrayList<Integer> subscr = executor.execQuery(connection, "SELECT thread_id FROM Subscribers WHERE user_email=?",
                        params1, result -> {
                            ArrayList<Integer> subscr1 = new ArrayList<>();
                            while (result.next()) {
                                subscr1.add(result.getInt(1));
                            }
                            return subscr1;
                        }
                );
                MyJSONObject resp = new MyJSONObject();
                resp.put("about", user.GetAbout());
                resp.put("email", user.GetEmail());
                resp.put("followers", followers);
                resp.put("following", following);
                resp.put("id", user.GetId());
                resp.put("isAnonymous", user.GetIsAn());
                resp.put("name", user.GetName());
                resp.put("subscriptions", subscr);
                resp.put("username", user.GetUsername());
                rez.put(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

    public MyJSONObject unfollowUser(String follower, String followee){
        try(Connection connection = ds.getConnection()) {
            ArrayList<String> params = new ArrayList<>(2);
            params.add(follower);
            params.add(followee);
            executor.execUpdate(connection, "DELETE FROM Follows WHERE follower=? AND following=?", params);
            return userDetails2(follower, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSONArray forumListUsers(String forum, Integer since_id, Integer limit, Boolean isAsc) {
        ArrayList<UserDataSet> users;
        ArrayList<String> params1 = new ArrayList<>(1);
        params1.add("");
        ArrayList<Object> params = new ArrayList<>(3);
        params.add(forum);
/*
        StringBuilder query =
                new StringBuilder("SELECT user_id, about, isAnonymous, name, username, Users.user_email FROM " +
                        "Users LEFT JOIN Posts ON Users.user_email=Posts.user_email WHERE forum_shortname = ?");
*/
        StringBuilder query =
                new StringBuilder("SELECT * FROM Users WHERE user_email IN" +
                                 "(SELECT DISTINCT user_email FROM Posts WHERE forum_shortname = ?)");
        if (since_id != null) {
            params.add(since_id);
            query.append(" AND user_id >= ?");
        }
        query.append(" ORDER BY name");
        if (!isAsc) {
            query.append(" DESC");
        }
        if (limit != null) {
            params.add(limit);
            query.append(" LIMIT ?");
        }
        MyJSONArray rez = new MyJSONArray();
        try (Connection connection = ds.getConnection()) {
            users = executor.execQuery(connection, query.toString(), params, result -> {
                int i = 0;
                while (result.next()) i++;
                result.beforeFirst();
                ArrayList<UserDataSet> users1 = new ArrayList<>(i);
                while (result.next()) {
                    users1.add(new UserDataSet(result.getInt("user_id"), result.getString("username"),
                            result.getString("user_email"), result.getString("about"),
                            result.getBoolean("isAnonymous"), result.getString("name")));
                }
                return users1;
            });
            for (UserDataSet user: users) {
                params1.set(0, user.GetEmail());
                ArrayList<String> followers = executor.execQuery(connection, "SELECT follower FROM Follows WHERE following=?",
                        params1, result -> {
                            ArrayList<String> followers1 = new ArrayList<>();
                            while (result.next()) {
                                followers1.add(result.getString(1));
                            }
                            return followers1;
                        }
                );
                ArrayList<String> following = executor.execQuery(connection, "SELECT following FROM Follows WHERE follower=?",
                        params1, result -> {
                            ArrayList<String> following1 = new ArrayList<>();
                            while (result.next()) {
                                following1.add(result.getString(1));
                            }
                            return following1;
                        }
                );
                ArrayList<Integer> subscr = executor.execQuery(connection, "SELECT thread_id FROM Subscribers WHERE user_email=?",
                        params1, result -> {
                            ArrayList<Integer> subscr1 = new ArrayList<>();
                            while (result.next()) {
                                subscr1.add(result.getInt(1));
                            }
                            return subscr1;
                        }
                );
                MyJSONObject resp = new MyJSONObject();
                resp.put("about", user.GetAbout());
                resp.put("email", user.GetEmail());
                resp.put("followers", followers);
                resp.put("following", following);
                resp.put("id", user.GetId());
                resp.put("isAnonymous", user.GetIsAn());
                resp.put("name", user.GetName());
                resp.put("subscriptions", subscr);
                resp.put("username", user.GetUsername());
                rez.put(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }

}
