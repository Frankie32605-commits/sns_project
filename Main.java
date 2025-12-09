import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Network network = new Network();
        Scanner scanner = new Scanner(System.in);
        
        //Example usage
        network.addFriend("Alice", "Bob");
        network.addFriend("Alice", "Charlie");
        network.displayNetwork();

        // Example Sorting
        System.out.println("Top Influencers: ");
        List<User> sorted = network.userFollowerSort(/* Pass user data here */);
        for (User u : sorted) {
            System.out.println(u.username + " with " + network.getFriends(u.username).size() + " friends.");
        }
    }
}