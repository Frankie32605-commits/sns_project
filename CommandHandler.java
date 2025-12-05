public class CommandHandler {
    public static void main(String[] args) {
        sns_network network = new sns_network();
        
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