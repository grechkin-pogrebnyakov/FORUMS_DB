package base;

import org.json.JSONArray;
import utils.MyJSONArray;
import utils.MyJSONObject;


public interface DatabaseService {
    String clear();
    MyJSONObject createForum(String name, String short_name, String user);
    MyJSONObject createUser(String username ,String about, String name, String email, Boolean isAn);
    MyJSONObject userDetails(String email);
    MyJSONObject forumDetails (String short_name, boolean rel_user);
    MyJSONObject createThread(String forum, String title, Boolean isClosed, Boolean isDeleted, String user, String date, String message, String slug);
    MyJSONObject threadDetails(int thread, boolean rel_user, boolean rel_forum);
    MyJSONObject createPost(String forum, Integer thread, Integer parent, Boolean isDeleted, Boolean isApproved, Boolean isEdited, Boolean isHighlighted, Boolean isSpam, String user, String date, String message);
    MyJSONObject postDetails(Integer post_id, boolean rel_user, boolean rel_forum, boolean rel_thread);
    MyJSONArray postList(String parent_id, boolean thread_set, boolean rel_user, boolean rel_forum, boolean rel_thread, String since, Integer limit, Boolean isAsc);
    int removePost(Integer post_id);

    int restorePost(Integer post_id);

    MyJSONObject updatePost(Integer post_id, String message);

    MyJSONObject votePost(Integer post_id, Integer vote);

    MyJSONArray threadList(String parent, boolean user_set, boolean rel_user, boolean rel_forum, String since, Integer limit, Boolean isAsc);

    MyJSONArray threadListPostsTree(Integer thread_id, String since, Integer limit, Boolean isAsc);

    MyJSONArray threadListPostsParentTree(Integer thread_id, String since, Integer limit, Boolean isAsc);

    int removeThread(Integer thread_id);

    int restoreThread(Integer thread_id);

    int closeThread(Integer thread_id);

    int openThread(Integer thread_id);

    MyJSONObject updateThread(Integer thread_id, String message, String slug);

    MyJSONObject voteThread(Integer thread_id, Integer vote);

    void subscribeThread(Integer thread_id, String user);

    void unsubscribeThread(Integer thread_id, String user);

    MyJSONArray userListPosts(String user, String since, Integer limit, Boolean isAsc);

    MyJSONObject updateUserProfile(String email, String name, String about);

    MyJSONObject followUser(String follower, String followee);

    MyJSONArray userListFollowers(String user, Integer since_id, Integer limit, Boolean isAsc);

    MyJSONArray userListFollowing(String user, Integer since_id, Integer limit, Boolean isAsc);

    MyJSONObject unfollowUser(String follower, String followee);

    MyJSONArray forumListUsers(String forum, Integer since_id, Integer limit, Boolean isAsc);
}
