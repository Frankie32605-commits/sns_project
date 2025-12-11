package model;
import java.time.LocalDateTime;
public class Post implements Comparable<Post> { 
    // Static counter for ensuring a unique ID for every post
    private static int nextId = 0;
    private final User author;
    private final String content;
    private final LocalDateTime timestamp;
    private final int postId; // Unique identifier for tie-breaking

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.postId = nextId++; // Assign and increment
    }

    public User getAuthor(){
        return author; 
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getTimestamp() {
        return timestamp; 
    }
    
    /**
     * Compares two posts. This is crucial for the AVLTree insertion and sorting.
     * The primary sort order is by timestamp (Newest first: Descending).
     * The secondary sort order (tie-breaker) is by unique postId (Ascending).
     */
    @Override
    public int compareTo(Post other) {
        //Primary Sort: Compare by timestamp (other vs. this) to get DESCENDING order (newest first)
        int timeComparison = other.timestamp.compareTo(this.timestamp);
        
        //Secondary Sort (Tie-breaker): If timestamps are identical, use the postId
        if (timeComparison == 0) {
            // Ascending order by postId ensures that no posts are treated as duplicates 
            // and discarded by the AVLTree (where compareTo == 0 means "equal").
            return Integer.compare(this.postId, other.postId);
        }
        
        return timeComparison;
    }

    @Override
    public String toString() {
        // Includes the unique Post ID for debugging, though often excluded in production
        return author.id + ": " + content + " [" + timestamp + " - Post ID: " + postId + "]";
    } 
}