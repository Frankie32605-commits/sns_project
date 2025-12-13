package api;

import model.Network;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface for World News API (https://worldnewsapi.com/)
 * Fetches news headlines by topic and posts them to the social network.
 * Uses basic Java I/O and RegEx for parsing (no external libraries).
 */

public class API_Interface {
    private static final String BASE_URL = "https://api.worldnewsapi.com/search-news";
    private static final String API_KEY = "3fdae895d43d4be687bd86d50b4286b0";

    /**
     * Fetches news articles for a topic and posts them to the network.
     * @param network The social network to post to.
     * @param topic Search query (e.g., "technology", "sports", etc.).
     */
    public static void fetchNewsAndPost(Network network, String topic) {
        try {
            List<NewsArticle> articles = fetchArticles(topic);
            
            if (articles.isEmpty()) {
                System.out.println("No news found for topic: '" + topic + "'");
                return;
            }
            
            System.out.println("\n--- Latest News: " + topic + " ---");
            int count = 1;
            
            for (NewsArticle article : articles) {
                String cleanSource = cleanSourceName(article.source);
                network.addPost(cleanSource, article.title);
                
                // Display each article as it's posted
                System.out.println(count + ". [" + cleanSource + "] " + article.title);
                count++;
            }
            
            System.out.println("\nSuccessfully posted " + articles.size() + " news articles!");
        } catch (Exception e) {
            System.out.println("News API fetch failed: " + e.getMessage());
            e.printStackTrace(); // Shows full error details
        }
    }

    /**
     * Fetches articles using HTTP request and regex parsing.
     * @param topic Search query.
     * @return List of parsed NewsArticle objects.
     * @throws Exception on API/network errors.
     */
    public static List<NewsArticle> fetchArticles(String topic) throws Exception {
        // Build URL with query parameters
        String urlString = BASE_URL + "?text=" + java.net.URLEncoder.encode(topic, "UTF-8") + "&language=en&number=5";
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // CRITICAL: API key goes in the header, not the URL
        conn.setRequestProperty("x-api-key", API_KEY);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        
        if (responseCode != 200) {
            // Read error response to see what went wrong
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorMsg = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorMsg.append(line);
            }
            errorReader.close();
            throw new Exception("HTTP " + responseCode + ": " + errorMsg.toString());
        }

        // Read successful response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseArticles(response.toString());
    }

    /**
     * Parses JSON response to extract news articles using regex.
     * @param json The JSON response string.
     * @return List of NewsArticle objects.
     */
    private static List<NewsArticle> parseArticles(String json) {
        List<NewsArticle> articles = new ArrayList<>();
        
        // More flexible pattern: finds title and source anywhere in each article object
        // Pattern looks for: "title":"<text>" and "source":"<text>" 
        Pattern titlePattern = Pattern.compile("\"title\"\\s*:\\s*\"([^\"]+)\"");
        Pattern sourcePattern = Pattern.compile("\"source\"\\s*:\\s*\"([^\"]+)\"");
        
        // Split by article boundaries (each starts with opening brace in the news array)
        String[] parts = json.split("\\{");
        
        for (String part : parts) {
            if (!part.contains("\"title\"")) continue; // Skip non-article parts
            
            Matcher titleMatcher = titlePattern.matcher(part);
            Matcher sourceMatcher = sourcePattern.matcher(part);
            
            if (titleMatcher.find()) {
                NewsArticle article = new NewsArticle();
                article.title = unescapeJson(titleMatcher.group(1));
                
                // Source might be optional
                if (sourceMatcher.find()) {
                    article.source = unescapeJson(sourceMatcher.group(1));
                } 
                else {
                    article.source = "Unknown";
                }
                
                articles.add(article);
            }
        }
        
        return articles;
    }

    /**
     * Unescapes JSON string values (handles quotes, newlines, Unicode characters, etc.)
     */
    private static String unescapeJson(String str) { 
        return str.replace("\\\"", "\"")
                  .replace("\\n", " ")
                  .replace("\\r", "")
                  .replace("\\t", " ")
                  // Replace smart quotes with regular quotes
                  .replace("\u201C", "\"")  // Left double quote
                  .replace("\u201D", "\"")  // Right double quote
                  .replace("\u2018", "'")   // Left single quote
                  .replace("\u2019", "'")   // Right single quote / apostrophe
                  .replace("\u2013", "-")   // En dash
                  .replace("\u2014", "-")   // Em dash
                  .replace("\u2026", "...")  // Ellipsis
                  // Remove other problematic Unicode characters
                  .replaceAll("[^\\x00-\\x7F]", "") // Remove non-ASCII if still present
                  .trim();
    }

    /**
     * Cleans source names (e.g., "Reuters via Yahoo" → "Reuters").
     * @param sourceName The raw source name string.
     * @return Cleaned source name.
     */
    private static String cleanSourceName(String sourceName) {
        if (sourceName == null || sourceName.trim().isEmpty()) {
            return "WorldNews";
        }
        
        // Remove " via XXX" suffixes
        int viaIndex = sourceName.indexOf(" via ");
        if (viaIndex != -1) {
            sourceName = sourceName.substring(0, viaIndex).trim();
        }
        
        // Additional Cleaning/Normalization
        if (sourceName.startsWith("AP")) return "AP News";
        if (sourceName.startsWith("BBC")) return "BBC";
        if (sourceName.startsWith("CNN")) return "CNN";
        
        return sourceName;
    }
}