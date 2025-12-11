package adts;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an undirected, weighted graph using an adjacency list.
 * Includes implementations for unweighted BFS (shortest path by hops)
 * and weighted Dijkstra's algorithm (shortest path by weight).
 *
 * @param <T> The type of the vertex (e.g., String for User ID).
 */
public class Graph<T> {
    /**
     * Represents a weighted edge in the graph.
     * Implements Comparable for use in the PriorityQueue (min-heap) in Dijkstra's.
     */
    private static class Edge<T> implements Comparable<Edge<T>> {
        public final T destination;
        public final int weight;

        public Edge(T destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge<T> other) {
            return Integer.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return destination.toString(); // For display purposes
        }
    }

    //Stores a list of Edge objects instead of just vertices
    private final Map<T, List<Edge<T>>> adjList = new HashMap<>();

    public void addVertex(T v) {
        adjList.putIfAbsent(v, new ArrayList<>());
    }

    /**
     * Adds an undirected, weighted edge (friendship) between two vertices.
     * @param a Vertex A (User A)
     * @param b Vertex B (User B)
     * @param weight The weight of the edge (e.g., interaction count)
     */
    public void addEdge(T a, T b, int weight) {
        addVertex(a);
        addVertex(b);
        // Undirected graph: Add a weighted edge from A to B
        adjList.get(a).add(new Edge<>(b, weight));
        // Undirected graph: Add a weighted edge from B to A (same weight for symmetry)
        adjList.get(b).add(new Edge<>(a, weight));
    }

    /**
     * Gets all neighboring vertices (destinations only).
     * @param v The vertex.
     * @return List of neighboring vertices.
     */
    public List<T> getNeighbors(T v) {
        return adjList.getOrDefault(v, List.of()).stream()
                .map(edge -> edge.destination)
                .collect(Collectors.toList());
    }
    
    public List<T> bfs(T start, T end) {
        if (!adjList.containsKey(start) || !adjList.containsKey(end)) {
            return List.of();
        }

        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        Map<T, T> parent = new HashMap<>();

        visited.add(start);
        queue.offer(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (current.equals(end)) {
                // Path found, reconstruct and return
                List<T> path = new ArrayList<>();
                for (T i = end; i != null; i = parent.get(i)) {
                    path.add(i);
                }
                Collections.reverse(path);
                return path;
            }

            // Iterate over the actual neighbors (destinations from the edges)
            for (Edge<T> edge : adjList.get(current)) {
                T neighbor = edge.destination;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                    parent.put(neighbor, current);
                }
            }
        }
        return List.of(); // returns if no path
    }
    
    // Weighted Dijkstra's Algorithm (Shortest Path by Weight)
    /**
     * Finds the shortest path weights from a start vertex to all others
     * in the weighted graph using Dijkstra's algorithm (with a Min-Heap).
     * * @param start The starting vertex.
     * @return A Map where the key is the destination vertex and the value is the 
     * total minimum weight (closeness) to reach it.
     */
    public Map<T, Integer> dijkstra(T start) {
        if (!adjList.containsKey(start)) {
            return Map.of();
        }
        
        // Stores the shortest distance from 'start' to every other vertex
        Map<T, Integer> distances = new HashMap<>();
        
        // Priority Queue (Min-Heap) stores edges, prioritized by their cumulative weight (distance)
        PriorityQueue<Edge<T>> pq = new PriorityQueue<>();

        //Initialize distances: start=0, all others=infinity
        for (T v : adjList.keySet()) {
            distances.put(v, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        //Initial state: Edge from start to start with 0 weight
        pq.offer(new Edge<>(start, 0));

        //Main Dijkstra's loop
        while (!pq.isEmpty()) {
            Edge<T> currentEdge = pq.poll();
            T u = currentEdge.destination;
            int dist_u = currentEdge.weight;

            //Optimization: If we found a shorter path to u already, skip
            if (dist_u > distances.get(u)) {
                continue;
            }

            //Check all neighbors (edges) of the current vertex u
            for (Edge<T> edge_uv : adjList.get(u)) {
                T v = edge_uv.destination;
                int weight_uv = edge_uv.weight;
                int newDist = dist_u + weight_uv;

                // Relaxation step: If a shorter path to v is found through u
                if (newDist < distances.get(v)) {
                    distances.put(v, newDist);
                    // Offer a new edge representing the distance to v from the start
                    // This uses the Edge class's compareTo based on the total distance
                    pq.offer(new Edge<>(v, newDist)); 
                }
            }
        }
        
        // Filter out unreachable nodes (max value) and the starting node for a clean result
        return distances.entrySet().stream().filter(entry -> entry.getValue() != Integer.MAX_VALUE && !entry.getKey().equals(start)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}