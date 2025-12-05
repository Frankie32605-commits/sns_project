import model.Network;
import model.User;
import java.util.List;
public class CommandHandler {
    public static void printActiveUsers(Network network, int limit) {
        List<User> activeUsers = network.getUsersSortedByActivity(limit);
        System.out.println("=== Most Active Users ===");
        int rank = 1;
        for (User u : activeUsers) {
            System.out.printf("%3d. %-15s → %d post%s%n",
                rank++, u.id, u.postCount, u.postCount == 1 ? "" : "s");
            if (limit > 0 && rank > limit) break;
        }
    }

    public static void printFollowers(Network network, int limit) {
        List<User> influencers = network.getUsersSortedByFollowers();
        System.out.println("=== Top Influencers (by friends) ===");
        int rank = 1;
        for (User u : influencers) {
            System.out.printf("%3d. %-15s → %d friend%s%n",
                rank++, u.id, u.getFriendCount(), u.getFriendCount() == 1 ? "" : "s");
            if (limit > 0 && rank > limit) break;
        }
    }
}