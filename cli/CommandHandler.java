package cli;
import model.Network;
import model.User;
import api.API_Interface;

public class CommandHandler {
    public static void printHelp() {
        System.out.println("user | friend | post | path | rank followers | rank active [n] | quit");
    }

    public static void handleRank(Network n, String[] p) {
        if (p.length < 2) return;
        if ("followers".equalsIgnoreCase(p[1])) {
            System.out.println("\nTop Influencers:");
            int r = 1;
            for (User u : n.getUsersSortedByFollowers()) {
                System.out.printf("%2d. %s → %d friends%n", r++, u.id, u.getFriendCount());
            }
        }
        if ("active".equalsIgnoreCase(p[1])) {
            int lim = p.length > 2 ? Integer.parseInt(p[2]) : 10;
            System.out.printf("\nMost Active (top %d):%n", lim);
            int r = 1;
            for (User u : n.getUsersSortedByActivity(lim)) {
                System.out.printf("%2d. %s → %d posts%n", r++, u.id, u.postCount);
            }
        }
    }

    public static void handleNews(Network network, String[] parts) {
    if (parts.length < 2) {
        System.out.println("Usage: news <topic> (e.g. news technology)");
        return;
    }
    API_Interface.fetchNewsAndPost(network, parts[1]);
    System.out.println("Fetching news about '" + parts[1] + "'...");
}
}