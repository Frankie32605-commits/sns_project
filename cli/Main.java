package cli;

import model.Network;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Network network = new Network();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Social Network Simulator - type 'help'");

        while (true) {
            System.out.print("\n> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 4);

            String command = parts[0].toLowerCase();

            switch (command) {
                case "quit", "exit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                case "help" -> CommandHandler.printHelp();
                case "user" -> { if (parts.length > 1) network.addUser(parts[1]); }
                case "friend" -> { if (parts.length > 2) network.addFriendship(parts[1], parts[2]); }
                case "post" -> { if (parts.length > 2) network.addPost(parts[1], line.substring(line.indexOf(parts[2]))); }
                case "path" -> { if (parts.length > 2) {
                    var path = network.shortestPath(parts[1], parts[2]);
                    System.out.println(path.isEmpty() ? "No path" : String.join(" → ", path));
                }}
                case "rank" -> CommandHandler.handleRank(network, parts);
                case "news" -> CommandHandler.handleNews(network, parts); 
                default -> System.out.println("Unknown command"); 
            }
        }  
    }
}