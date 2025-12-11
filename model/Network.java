package model;
import adts.Graph;
import adts.Sorts;
import java.util.*;
import java.util.stream.Collectors;

public class Network {
    //Using an Adjacency List representation for the social network graph
    //The "Database" of all users in the network
    //private Map<String, Set<String>> networkGraph;
    private final Map<String, User> users = new HashMap<>();
    private final adts.Graph<String> graph = new Graph<>();

    //Add a user to the network

    public User addUser(String username) {
        return users.computeIfAbsent(username, User::new);
    }
    //Add a friendship (edge) between two users
    public void addFriendship(String a, String b){
        User userA = addUser(a);
        User userB = addUser(b);
        graph.addEdge(a, b, 1); //undirected edge

        //Add mutual connection
        userA.addFriend(userB);
        userB.addFriend(userA);
    }

    public void addPost(String name, String content){
        addUser(name).addPost(new Post(addUser(name), content));
    }

    //Get friends List of a user
    public Set<User> getFriends(String username) {
        User user = users.get(username);
        if(user == null) return Set.of();
        return Collections.unmodifiableSet(user.friends);
    }

    public Set<User> getMutualFriends(String userA, String userB) {
        Set<User> friendsA = getFriends(userA);
        Set<User> friendsB = getFriends(userB);

        //Create a copy of the first set to retain only mutual friends
        Set<User> mutualFriends = new HashSet<>(friendsA);

        // Keep only elements that appear in both sets (Intersection)
        mutualFriends.retainAll(friendsB);
        return mutualFriends;
    }

    //Display the entire network (Debugging helper)
    public void displayNetwork() {
        System.out.println("--- Current Social Network ---");
        for (User u : users.values()) {
            System.out.print(u.id + " → [");
            System.out.print(u.friends.stream().map(f -> f.id).collect(Collectors.joining(", ")));
            System.out.println("]");
        }
        System.out.println("-----------------------------------");
}

    //Arranges users by their follower count
    public List<User> getUsersSortedByFollowers(){ 
        //This method ranks the users by the amount of friends/followers they have
        List<User> list = new ArrayList<>(users.values());
        Sorts.heapSort(list, Comparator.comparingInt(u -> -u.getFriendCount()));
        return list;
    }

    //Arranges users by their activity (number of posts)
    public List<User> getUsersSortedByActivity(int limit){
        List<User> allUsers = new ArrayList<>(users.values());

        //descending by number of posts
        Sorts.heapSort(allUsers, Comparator.comparingInt(u -> -u.postCount));
        return limit <= 0 ? allUsers : allUsers.subList(0, Math.min(limit, allUsers.size()));
    }

    public User getUser(String username){
        return users.get(username);
    } 

    public Graph<String> getGraph(){
        return graph;
    }

    /**
     * Public entry method to check if a friendship cycle exists anywhere in the network.
     * Starts DFS from every unvisited user to handle disconnected graphs.
     * @return true if a cycle is found, false otherwise.
     */
    public boolean hasFriendshipCycle() {
        Set<User> visited = new HashSet<>();
        // Iterate through all users to ensure all parts of the graph are checked
        for (User user : users.values()) {
            if (!visited.contains(user)) {
                // If a cycle is found starting from this unvisited user, return true immediately.
                if (dfsFindCycle(user, null, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method using Recursive Depth-First Search (DFS) to detect a cycle.
     * @param current The user currently being visited.
     * @param parent The user that led to the current user (to ignore back-edges).
     * @param visited The set of users already visited.
     * @return true if a cycle is detected, false otherwise.
     */
    private boolean dfsFindCycle(User current, User parent, Set<User> visited) {
        // Mark the current node as visited
        visited.add(current);

        // Check all friends (neighbors) of the current user
        for (User neighbor : current.friends) {
            if (!visited.contains(neighbor)) {
                // Case 1: Neighbor is not visited. Continue the search recursively.
                if (dfsFindCycle(neighbor, current, visited)) {
                    return true;
                }
            } 
            else if (parent != null && !neighbor.equals(parent)) {
                // Case 2: Neighbor is visited AND is NOT the direct parent.
                // This means we found a path back to an ancestor, which forms a cycle.
                System.out.println("Cycle detected involving users: " + current.id + " and " + neighbor.id);
                return true;
            }
        }
        return false;
    }

    public List<String> shortestPath(String a, String b){
        return graph.bfs(a, b);
    }
}   