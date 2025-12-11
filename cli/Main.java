package cli;

import model.Network;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Network network = new Network();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Social Network Simulator - type 'help'\n");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            // Split line into command and arguments, allowing 4 parts max
            String[] parts = line.split("\\s+", 4);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "quit", "exit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                case "help" -> CommandHandler.printHelp();

                case "user" -> {
                    if (parts.length > 1) {
                        network.addUser(parts[1]);
                        System.out.println("User '" + parts[1] + "' added.");
                    } else {
                        System.out.println("Usage: user <name>");
                    }
                }

                case "friend" -> {
                    if (parts.length > 2) {
                        network.addFriendship(parts[1], parts[2]);
                        System.out.println(parts[1] + " and " + parts[2] + " are now friends!");
                    } 
                    else {
                        System.out.println("Usage: friend <userA> <userB>");
                    }
                }

                case "post" -> {
                    if (parts.length > 2) {
                        // Capture the rest of the line as the message content
                        String message = line.substring(line.indexOf(parts[2]));
                        network.addPost(parts[1], message);
                        System.out.println(parts[1] + " posted: " + message);
                    } 
                    else {
                        System.out.println("Usage: post <name> <message>");
                    }
                }

                case "path" -> {
                    if (parts.length > 2) {
                        var path = network.shortestPath(parts[1], parts[2]);
                        System.out.println(path.isEmpty() 
                            ? "No path found between " + parts[1] + " and " + parts[2]
                            : "Shortest path: " + String.join(" -> ", path));
                    } else {
                        System.out.println("Usage: path <userA> <userB>");
                    }
                }
                
                //Calls the handler for Dijkstra's/Closeness rankings
                case "closeness" -> CommandHandler.handleCloseness(network, parts);
                case "rank" -> CommandHandler.handleRank(network, parts);
                case "news" -> CommandHandler.handleNews(network, parts);
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        }
    }
}