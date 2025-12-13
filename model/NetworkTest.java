package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class NetworkTest {
    private Network net;

    @BeforeEach 
    void setup() { 
        net = new Network(); 
    }

    @Test 
    @DisplayName("Test Basic Friendship and Graph Vertex Addition")
    void testAddFriendship() {
        net.addFriendship("A", "B");
        assertEquals(1, net.getUser("A").getFriendCount());
        // Verify vertices exist in the underlying Graph ADT
        assertTrue(net.getGraph().getNeighbors("A").contains("B")); 
    }

    @Test 
    @DisplayName("Test Unweighted Shortest Path (BFS)")
    void testShortestPath() {
        net.addFriendship("A", "B");
        net.addFriendship("B", "C");
        net.addFriendship("A", "X"); // Distraction path
        
        // Shortest path A -> C is A -> B -> C
        assertEquals(List.of("A", "B", "C"), net.shortestPath("A", "C"));
        // Test no path exists
        assertEquals(List.of(), net.shortestPath("A", "Z")); 
    }

    @Test 
    @DisplayName("Test Follower Ranking (HeapSort)")
    void testFollowerRanking() {
        net.addUser("X"); 
        net.addUser("Y"); 
        net.addUser("Z");
        
        // X has 2 friends (Y, Z)
        net.addFriendship("X", "Y"); 
        net.addFriendship("X", "Z");
        // Y has 1 friend (X)
        // Z has 1 friend (X)
        
        // Expect X (2) > Y (1) and Z (1)
        List<User> sortedUsers = net.getUsersSortedByFollowers();
        assertEquals("X", sortedUsers.get(0).id);
        
        // Check ties: Y and Z have the same count (order is arbitrary but check count)
        assertEquals(2, sortedUsers.get(0).getFriendCount());
        assertEquals(1, sortedUsers.get(1).getFriendCount());
        assertEquals(1, sortedUsers.get(2).getFriendCount());
    }

    @Test 
    @DisplayName("Test Activity Ranking (HeapSort)")
    void testActivityRanking() {
        net.addPost("Alice", "post 1");
        net.addPost("Alice", "post 2"); // Alice: 2 posts
        net.addPost("Bob", "post 1");  // Bob: 1 post
        net.addUser("Charlie");       // Charlie: 0 posts
        
        // Expect Alice (2) > Bob (1) > Charlie (0)
        List<User> sortedUsers = net.getUsersSortedByActivity(10);
        assertEquals("Alice", sortedUsers.get(0).id);
        assertEquals("Bob", sortedUsers.get(1).id);
        assertEquals("Charlie", sortedUsers.get(2).id);
        
        // Check limit
        assertEquals(2, net.getUsersSortedByActivity(2).size());
    }

    @Test 
    @DisplayName("Test AVL Post Ordering (Newest First) and Uniqueness")
    void testAVLOrder() throws InterruptedException {
        User u = net.addUser("TestUser");
        
        // 1. Post A (Oldest)
        u.addPost(new Post(u, "A - First Post")); 
        Thread.sleep(1); // Ensure distinct timestamps
        
        // 2. Post B (Newest)
        u.addPost(new Post(u, "B - Second Post")); 
        
        // Expect descending order (Newest first) due to Post.compareTo fix
        List<Post> postsInOrder = u.posts.getInOrder();
        
        assertEquals(2, postsInOrder.size());
        assertEquals("B - Second Post", postsInOrder.get(0).getContent()); // Newest
        assertEquals("A - First Post", postsInOrder.get(1).getContent()); // Oldest
    }
    
    @Test 
    @DisplayName("Test DFS Cycle Detection (Positive)")
    void testHasFriendshipCycle_Positive() {
        // Cycle: A -- B -- C -- A
        net.addFriendship("A", "B");
        net.addFriendship("B", "C");
        net.addFriendship("C", "A"); // Closes the cycle
        
        assertTrue(net.hasFriendshipCycle(), "Should detect a cycle in A-B-C-A.");
    }

    @Test 
    @DisplayName("Test DFS Cycle Detection (Negative)")
    void testHasFriendshipCycle_Negative() {
        // Tree structure (no cycle): X -- Y, X -- Z
        net.addFriendship("X", "Y");
        net.addFriendship("X", "Z");
        
        assertFalse(net.hasFriendshipCycle(), "Should not detect a cycle in a tree structure.");
    }
    
    @Test 
    @DisplayName("Test DFS Cycle Detection (Disconnected Graph)")
    void testHasFriendshipCycle_Disconnected() {
        // Disconnected graph: A--B (no cycle), and C--D--E--C (cycle)
        net.addFriendship("A", "B");
        
        net.addFriendship("C", "D");
        net.addFriendship("D", "E");
        net.addFriendship("E", "C"); // Cycle here
        
        assertTrue(net.hasFriendshipCycle(), "Should detect cycle in disconnected component C-D-E-C.");
    }

    @Test 
    @DisplayName("Test Dijkstra's Weighted Closeness (Simple Path)")
    void testDijkstraSimple() {
        // A --(1)--> B --(1)--> C
        // Note: The graph is currently set to use a default weight of 1 per friendship.
        net.addFriendship("A", "B");
        net.addFriendship("B", "C");
        
        Map<String, Integer> rankings = net.getClosenessRankings("A");
        
        // Path A->B is 1. Path A->C is 2.
        assertEquals(1, rankings.get("B"));
        assertEquals(2, rankings.get("C"));
        assertFalse(rankings.containsKey("A"), "Start node should be excluded.");
        assertFalse(rankings.containsKey("D"), "Unconnected node should be excluded.");
    }

    @Test 
    @DisplayName("Test Dijkstra's Weighted Closeness (Shorter Path Found)")
    void testDijkstraShorterPath() throws Exception {
        // Path 1: Alice -(1)- Bob -(1)- Carol. Total: 2
        net.addFriendship("Alice", "Bob");
        net.addFriendship("Bob", "Carol");
        
        // Path 2: Alice -(1)- David -(1)- Carol. Total: 2
        net.addFriendship("Alice", "David");
        net.addFriendship("David", "Carol");

        Map<String, Integer> rankings = net.getClosenessRankings("Alice");
        
        // Both paths Alice -> Carol are 2 hops (weight 2).
        assertEquals(1, rankings.get("Bob"));
        assertEquals(1, rankings.get("David"));
        assertEquals(2, rankings.get("Carol"));
    }
} 