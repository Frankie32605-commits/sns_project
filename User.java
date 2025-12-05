import adts.AVLTree;
import java.util.HashSet;
import java.util.Set;

public class User {
    //private Map<String, User> users;
    //private int nextUserId;

    //Individual User Attributes
    public final String id;
    public final String username; //Added to store the name of the user
    public int postCount = 0;
    public AVLTree<Post> posts = new AVLTree<>();
    public final Set<User> friends = new HashSet<>();

    //Constructor 1: "The Manager"
    //Manages the collection of users in the social network
    public User(String id) {
        //this.users = new HashMap<>();
        //this.nextUserId = 1;
        this.id = id; 
        this.id = "ROOT";
        this.username = "NetworkManager";;
    }

    //Constructor 2: "The Individual" (Private)
    private User(int id, String username) {
        this.id = Integer.toString(id);
        this.username = username;
        this.users = null; // Individual users do not manage other users
    }

    public void addPost(Post post){
        posts.insert(post);
        this.postCount++;
    }
    
    public void addFriend(User friend){
        friends.add(friend);
    }

    public int getFriendCount(){
        return friends.size();
    }
    
    private int countPosts(User user){
        return user.postCount;
    }

    private static void printActiveUsers(Network network, int limit){
        //List<User> activeUsers = network.getUsersSortedByActivity(limit);
        System.out.println(); //we should discuss how this should be formatted at some point; will get back to this
    }

    @Override
    public String toString(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }
}