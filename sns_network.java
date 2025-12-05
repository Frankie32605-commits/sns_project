import java.util.*;

public class sns_network {
    //Using an Adjacency List representation for the social network graph
    //The "Database" of all users in the network
    private Map<String, Set<String>> networkGraph;

    //Constructor
    public sns_network() {
        this.networkGraph = new HashMap<>();
    }

    public static void main(String[] args) {
        sns_network = new sns_network();
        
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

    //Add a user to the network

    public User addUser(String username) {
        if (users.containsKey(username)) {
            // Error handling for existing user
            return users.get(username);
        }
        User user = new User(username);
        users.put(username, user);
        graph.addVertex(username);
        return user;
    }
    //Add a friendship (edge) between two users
    public void addFriend(String userA, String userB) {
        addUser(userA);
        addUser(userB);

        //Add mutual connection
        networkGraph.get(userA).add(userB);
        networkGraph.get(userB).add(userA);
    }

    //Get friends List of a user
    public Set<String> getFriends(String person) {
        return networkGraph.getOrDefault(person, new HashSet<>());
    }

    public Set<String> getMutualFriends(String userA, String userB) {
        Set<String> friendsA = getFriends(userA);
        Set<String> friendsB = getFriends(userB);

        //Create a copy of the first set to retain only mutual friends
        Set<String> mutualFriends = new HashSet<>(friendsA);

        // Keep only elements that appear in both sets (Intersection)
        mutualFriends.retainAll(friendsB);
        return mutualFriends;
    }

    //Display the entire network (Debugging helper)
    public void displayNetwork() {
        System.out.println("--- Current Social Network status ---");
        for (String user : networkGraph.keySet()) {
            System.out.println(user + " is friends with: " + networkGraph.get(user));
        }
        System.out.println("--------------------------------------");
    }

    //Arranges users by their follower count
    public List<User> userFollowerSort(List<User> users){ 
        //This method ranks the users by the amount of friends/followers they have
        List<User> allUsers = new ArrayList<>(users.values());
        Sorts.heapSort(allUsers, Comparator.comparingInt(u -> -graph.getNeighbors(u.id).size()));
        return allUsers;
    }

    //Arranges users by their activity (number of posts)
    public List<User> getUsersSortedByActivity(int limit){
        List<User> allUsers = new ArrayList<>(users.values());

        //descending by number of posts
        Sorts.heapSort(allUsers, Comparator.comparingInt(u -> -countPosts(u)));

        return limit <= 0 ? allUsers : allUsers.subList(0, Math.min(limit, allUsers.size()));
    } 
}