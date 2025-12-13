package cli;

import model.Network;
import model.User;
import api.API_Interface;
import java.util.Map;
import java.util.Comparator;

public class CommandHandler {
    
    public static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  user <name> ..................... Add a new user.");
        System.out.println("  friend <A> <B> .................. Add a mutual friendship.");
        System.out.println("  post <name> <message> ........... Create a new post.");
        System.out.println("  path <A> <B> .................... Find shortest path (BFS).");
        System.out.println("  closeness <user> ................ Rank users by weighted closeness (Dijkstra's).");
        System.out.println("  rank followers .................. Rank users by friend count (HeapSort).");
        System.out.println("  rank active [n] ................. Rank users by post count (HeapSort).");
        System.out.println("  news <topic> .................... Fetch news and post to network.");
        System.out.println("  quit | exit ..................... Exit the simulator.");
    }

    public static void handleRank(Network n, String[] p) {
        if (p.length < 2) {
            System.out.println("Usage: rank followers OR rank active [n]");
            return;
        }

        if ("followers".equalsIgnoreCase(p[1])) {
            System.out.println("\n--- Top Influencers (Ranked by Friends) ---");
            int r = 1;
            for (User u : n.getUsersSortedByFollowers()) {
                System.out.printf("%2d. %s -> %d friends%n", r++, u.id, u.getFriendCount());
            }
        } 
        else if ("active".equalsIgnoreCase(p[1])) {
            int lim;
            try {
                lim = p.length > 2 ? Integer.parseInt(p[2]) : 10;
            } catch (NumberFormatException e) {
                 System.out.println("Invalid number for limit.");
                 return;
            }
            
            System.out.printf("\n--- Most Active Users (Top %d) ---%n", lim);
            int r = 1;
            for (User u : n.getUsersSortedByActivity(lim)) {
                System.out.printf("%2d. %s -> %d posts%n", r++, u.id, u.postCount);
            }
        } 
        else {
            System.out.println("Unknown rank type. Use 'followers' or 'active'.");
        }
    }
    
    /**
     * Handles the 'closeness' command, calling Dijkstra's algorithm.
     */
    public static void handleCloseness(Network n, String[] p) {
        if (p.length < 2) {
            System.out.println("Usage: closeness <user> (Ranks users by shortest weighted path)");
            return;
        }
        String startUser = p[1];
        if (n.getUser(startUser) == null) {
            System.out.println("Error: User '" + startUser + "' not found.");
            return;
        }

        Map<String, Integer> rankings = n.getClosenessRankings(startUser);
        
        System.out.println("\n--- Weighted Closeness from " + startUser + " (Lower Score = Closer) ---");
        
        if (rankings.isEmpty()) {
            System.out.println("No connections found to other users.");
            return;
        }
        
        // Sort the map results by distance (value, ascending)
        rankings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> 
                    System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue())
                );
    }

    public static void handleNews(Network network, String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: news <topic> (e.g. news technology)");
            return;
        }
        //Print the fetching message BEFORE the potentially long-running API call
        System.out.println("Fetching news about '" + parts[1] + "'...");
        API_Interface.fetchNewsAndPost(network, parts[1]);
    }
}