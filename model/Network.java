package model;

import adts.Graph;
import adts.Sorts;
import java.util.*;
import java.util.stream.Collectors;

public class Network {
    
    // The "Database" of all users
    private final Map<String, User> users = new HashMap<>();
    
    // The Graph ADT, now supporting weighted edges
    private final adts.Graph<String> graph = new Graph<>();

    // Add a user to the network
    public User addUser(String username) {
        // Ensures the user exists in both the map and the graph
        graph.addVertex(username); 
        return users.computeIfAbsent(username, User::new);
    }
    
    /**
     * Adds a friendship (edge) between two users.
     * Note: We are setting a default weight of 1 for a new friendship.
     */
    public void addFriendship(String a, String b){
        User userA = addUser(a);
        User userB = addUser(b);
        
        // Add edge to the Graph ADT (default weight 1)
        graph.addEdge(a, b, 1); 

        // Add mutual connection to the User objects
        userA.addFriend(userB);
        userB.addFriend(userA);
    }

    public void addPost(String name, String content){
        // Ensure user exists before trying to add a post
        addUser(name).addPost(new Post(addUser(name), content));
    }

    // Get friends List of a user
    public Set<User> getFriends(String username) {
        User user = users.get(username);
        if(user == null) return Set.of();
        return Collections.unmodifiableSet(user.friends);
    }

    public Set<User> getMutualFriends(String userA, String userB) {
        Set<User> friendsA = getFriends(userA);
        Set<User> friendsB = getFriends(userB);

        Set<User> mutualFriends = new HashSet<>(friendsA);
        mutualFriends.retainAll(friendsB);
        return mutualFriends;
    }

    public void displayNetwork() {
        System.out.println("--- Current Social Network ---");
        for (User u : users.values()) {
            System.out.print(u.id + " → [");
            System.out.print(u.friends.stream().map(f -> f.id).collect(Collectors.joining(", ")));
            System.out.println("]");
        }
        System.out.println("-----------------------------------");
    }

    // Arranges users by their follower count (HeapSort implementation)
    public List<User> getUsersSortedByFollowers(){ 
        List<User> list = new ArrayList<>(users.values());
        // Sort descending by friend count (using negative value to simulate max-heap for descending order)
        Sorts.heapSort(list, Comparator.comparingInt(u -> -u.getFriendCount()));
        return list;
    }

    // Arranges users by their activity (number of posts) (HeapSort implementation)
    public List<User> getUsersSortedByActivity(int limit){
        List<User> allUsers = new ArrayList<>(users.values());

        // Sort descending by number of posts
        Sorts.heapSort(allUsers, Comparator.comparingInt(u -> -u.postCount));
        return limit <= 0 ? allUsers : allUsers.subList(0, Math.min(limit, allUsers.size()));
    }
    
    /**
     * Ranks users by their weighted closeness (shortest path weight) to the start user.
     * @param startUser The user to rank from.
     * @return A map of User ID to their minimum weighted distance.
     */
    public Map<String, Integer> getClosenessRankings(String startUser) {
        if (!users.containsKey(startUser)) {
            return Map.of();
        }
        // Call the new Dijkstra's method on the Graph ADT
        return graph.dijkstra(startUser);
    }


    public User getUser(String username){
        return users.get(username);
    } 

    public Graph<String> getGraph(){
        return graph;
    }

    /**
     * Public entry method to check if a friendship cycle exists anywhere in the network.
     * Uses the recursive DFS approach, which is correct for undirected graphs.
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
     * Helper method using Recursive Depth-First Search (DFS) to detect a cycle in an UNDIRECTED graph.
     * @param current The user currently being visited.
     * @param parent The user that led to the current user (to ignore the trivial back-edge).
     * @param visited The set of users already visited.
     * @return true if a cycle is detected, false otherwise.
     */
    private boolean dfsFindCycle(User current, User parent, Set<User> visited) {
        visited.add(current);

        // Check all friends (neighbors) of the current user
        for (User neighbor : current.friends) {
            if (!visited.contains(neighbor)) {
                // Case 1: Neighbor is not visited. Continue the search recursively.
                if (dfsFindCycle(neighbor, current, visited)) {
                    return true;
                }
            } 
            // Case 2: Neighbor is visited AND is NOT the direct parent.
            else if (parent != null && !neighbor.equals(parent)) {
                // This means we found a path back to an ancestor, which forms a cycle.
                System.out.println("Cycle detected involving users: " + current.id + " and " + neighbor.id);
                return true;
            }
        }
        return false;
    }

    public List<String> shortestPath(String a, String b){
        // Uses the unweighted BFS in Graph.java
        return graph.bfs(a, b);
    }
}