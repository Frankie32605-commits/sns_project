import java.util.*;
public class Graph {
    private final Map<T, List<T>> adjList = new HashMap<>();

    //graph stuff
    public void addVertex(T v){
        adjList.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(T a, T b, int weight){
        addVertex(a);
        addVertex(b);
        adjList.get(a).add(b);
        adjList.get(b).add(a); //undirected graph
    }

    public List<T> getNeighbors(T v){
        return adjList.getOrDefault(v, List.of());
    }

    //bfs code
    public List<T> bfs(T start, T end){
        if(!adjList.containsKey(start) || !adjList.containsKey(end)){
            return List.of();
        }

        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        Map<T, T> parent = new HashMap<>();

        visited.add(start);
        queue.offer(start);
        parent.put(start, null);

        while(!queue.isEmpty()){
            T current = queue.poll();
            if(current.equals(end)){
                List<T> path = new ArrayList<>();
                for(T i = end; i != null; i = parent.get(i)){
                    path.add(i);
                }
                Collections.reverse(path);
                return path;
            }

            for(T neighbor : adjList.get(current)){
                if(!visited.contains(neighbor)){
                    visited.add(neighbor);
                    queue.offer(neighbor);
                    parent.put(neighbor, current);
                }
            }
        }
        return List.of(); //returns if no path
    }
}