package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class NetworkTest {
    private Network net;

    @BeforeEach void setup() { net = new Network(); }

    @Test void testAddFriendship() {
        net.addFriendship("A", "B");
        assertEquals(1, net.getUser("A").getFriendCount());
    }

    @Test void testShortestPath() {
        net.addFriendship("A", "B");
        net.addFriendship("B", "C");
        assertEquals(List.of("A", "B", "C"), net.shortestPath("A", "C"));
    }

    @Test void testFollowerRanking() {
        net.addUser("X"); net.addUser("Y"); net.addUser("Z");
        net.addFriendship("X", "Y"); net.addFriendship("X", "Z");
        assertEquals("X", net.getUsersSortedByFollowers().get(0).id);
    }

    @Test void testActivityRanking() {
        net.addPost("Alice", "hi");
        net.addPost("Alice", "yo");
        net.addPost("Bob", "hello");
        assertEquals("Alice", net.getUsersSortedByActivity(10).get(0).id);
    }

    @Test void testAVLOrder() {
        User u = net.addUser("Test");
        u.addPost(new Post(u, "first"));
        u.addPost(new Post(u, "second"));
        assertEquals(2, u.posts.getInOrder().size());
    }
}