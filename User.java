import adts.AVLTree;
import java.util.HashSet;
import java.util.Set;

public class User {
    //Individual User Attributes
    public final String id;
    public int postCount = 0;
    public AVLTree<Post> posts = new AVLTree<>();
    public final Set<User> friends = new HashSet<>();

    //Manages the collection of users in the social network
    public User(String id) {
        this.id = id;
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

    //Required for HashSet<User> to work, and prevents bugs

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

    @Override
    public String toString(){
        return id;
    }
}