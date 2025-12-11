package api;
import model.Network;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
//import com.google.gson.Gson;

/**
 * Interface for World News API (https://worldnewsapi.com/)
 * Fetches news headlines by topic and posts them to the social network.
 * Note: Assumes Gson library is available in the classpath.
 */
public class API_Interface {
    private static final String BASE_URL = "https://api.worldnewsapi.com/search-news";
    private static final String API_KEY = "3fdae895d43d4be687bd86d50b4286b0";
    private static final Gson GSON = new Gson();

    /**
     * Fetches news articles for a topic and posts them to the network.
     * @param network The social network to post to.
     * @param topic Search query (e.g., "technology", "Elon Musk").
     */
    public static void fetchNewsAndPost(Network network, String topic) {
        try {
            List<NewsArticle> articles = fetchArticles(topic);
            
            if (articles.isEmpty()) {
                System.out.println("No news found for topic: '" + topic + "'");
                return;
            }
            
            for (NewsArticle article : articles) {
                String cleanSource = cleanSourceName(article.source);
                network.addPost(cleanSource, article.title); // Use cleaned source as "user"
            }
            System.out.println("Posted " + articles.size() + " news articles from World News API!");
        } catch (Exception e) {
            System.out.println("News API fetch failed: " + e.getMessage());
        }
    }

    /**
     * Synchronous fetch that uses Gson for robust JSON parsing.
     * @param topic Search query.
     * @return List of parsed NewsArticle objects.
     * @throws Exception on API/network errors.
     */
    public static List<NewsArticle> fetchArticles(String topic) throws Exception {
        URL url = new URL(BASE_URL + "?text=" + java.net.URLEncoder.encode(topic, "UTF-8") + 
                             "&language=en&number=5");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-API-KEY", API_KEY);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            // Read error stream if available
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String errorLine;
            StringBuilder errorResponse = new StringBuilder();
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            throw new Exception("API error: HTTP " + responseCode + ". Response: " + errorResponse.toString());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        NewsResponse newsResponse = GSON.fromJson(response.toString(), NewsResponse.class);
        
        // Return a sublist of the articles array
        return newsResponse.news != null ? List.of(newsResponse.news) : List.of();
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
        
        // Additional Cleaning/Normalization (as per original logic)
        if (sourceName.startsWith("AP")) return "AP News";
        if (sourceName.startsWith("BBC")) return "BBC";
        if (sourceName.startsWith("CNN")) return "CNN";
        
        return sourceName;
    }
    
    // The previous extractHeadline method is no longer needed as the title is directly parsed.
}