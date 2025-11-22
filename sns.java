import java.util.*;
import java.util.list;
import java.util.ArrayList; 
import java.util.Math;

public class User {
    private Map<String, User> users;
    private int nextUserId;
    public int postCount = 0;
    public AVLTree<Post> posts = new AVLTree<>();

    public User() {
        this.users = new HashMap<>();
        this.nextUserId = 1; 
        
    }

    public void addPost(Post post){
        posts.insert(post);
        this.postCount++;
    }

    // Core Graph Operations

    public User addUser(String username) {
        if (users.containsKey(username)) {
            // Error handling for existing user
            return users.get(username);
        }
        User newUser = new User(nextUserId++, username);
        users.put(username, newUser);
        return newUser;
    }

    public void addFriendship(String userA_name, String userB_name) {
        User userA = users.get(userA_name);
        User userB = users.get(userB_name);

        if (userA == null || userB == null) {
            System.out.println("Error: One or both users have not been found.");
            return;
        }

        // Undirected Graph: Adding connections to both sides
        userA.addFriend(userB);
        userB.addFriend(userA);
    }

    public List<User> getTopInfluencers() {
        //Convert the HashMap values (User objects) into a list for sorting
        List<User> userList = new ArrayList<>(user.values());

        //Use the custom HeapSort implementations with the UserComparator
        Comparator<User> comparator = new UserComparator();
        HeapSortUsers.sort(userList, comparator);

        return userList;
    }
    
//thought the follower sort worked better in the sns network file
    public List<User> getUsersSortedByActivity(int limit){
        List<User> allUsers = new ArrayList<>(users.values());

        //descending by number of posts
        Sorts.heapSort(allUsers, Comparator.comparingInt(User u) -> -countPosts(u));

        return limit <= 0 ? allUsers : allUsers.subList(0, Math.min(limit, allUsers.size()));
    }

    private int countPosts(User user){
        return user.postCount;
    }

    private static void printActiveUsers(Network network, int limit){
        List<User> activeUsers = network.getUsersSortedByActivity(limit);
        System.out.println(); //we should discuss how this should be formatted at some point; will get back to this
    }

}