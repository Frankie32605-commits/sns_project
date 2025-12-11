
import java.time.LocalDateTime;

public class Post implements Comparable<Post> { 

    private final User author;
    private final String content;
    private final LocalDateTime timestamp;

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
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

    @Override
    public int compareTo(Post other) {
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public String toString() {
        return author.id + ": " + content + " [" + timestamp + "]";
    }
}