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
 * Interface for World News API[](https://worldnewsapi.com/)
 * Fetches news headlines by topic and posts them to the social network.
 * Free tier: 100 requests/day; requires X-API-KEY header.
 */
public class API_Interface {
    private static final String BASE_URL = "https://api.worldnewsapi.com/search-news";
    private static final String API_KEY = "3fdae895d43d4be687bd86d50b4286b0";

    /**
     * Fetches news articles for a topic and posts them to the network.
     * @param network The social network to post to.
     * @param topic Search query (e.g., "technology", "Elon Musk").
     */
    public static void fetchNewsAndPost(Network network, String topic) {
        // Synchronous fetch for simplicity (non-blocking in CLI context)
        try {
            List<String> articles = fetchArticles(topic);
            for (String article : articles) {
                // Clean source name and post
                String cleanSource = cleanSourceName(article);
                network.addPost(cleanSource, extractHeadline(article));
            }
            System.out.println("Posted " + articles.size() + " news articles from World News API!");
        } catch (Exception e) {
            System.out.println("News API fetch failed: " + e.getMessage());
        }
    }

    /**
     * Synchronous fetch for testing/JUnit.
     * @param topic Search query.
     * @return List of raw article strings (title + source).
     * @throws Exception on API/network errors.
     */
    public static List<String> fetchArticles(String topic) throws Exception {
        List<String> articles = new ArrayList<>();
        URL url = new URL(BASE_URL + "?text=" + java.net.URLEncoder.encode(topic, "UTF-8") + 
                          "&language=en&number=5");  // 5 articles for free tier

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-API-KEY", API_KEY);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("API error: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Simple JSON parsing for articles array
        String json = response.toString();
        // Find "articles": [ ... ] section
        int start = json.indexOf("\"articles\":[");
        if (start == -1) throw new Exception("Invalid JSON response");

        start += "\"articles\":[\"".length();
        int end = json.indexOf("]", start);
        String articlesJson = json.substring(start, end);

        // Extract up to 5 articles (title and source)
        Pattern articlePattern = Pattern.compile("\"title\":\"([^\"]+)\".*?\"source\":\"([^\"]+)\"");
        Matcher matcher = articlePattern.matcher(articlesJson);
        while (matcher.find() && articles.size() < 5) {
            String title = matcher.group(1);
            String source = matcher.group(2);
            articles.add("Title: " + title + " | Source: " + source);
        }

        return articles;
    }

    /**
     * Cleans source names (e.g., "Reuters via Yahoo" → "Reuters").
     * @param article Raw article string.
     * @return Cleaned source name.
     */
    private static String cleanSourceName(String article) {
        Pattern viaPattern = Pattern.compile("Source: ([^|]+)(?: via .+)?");
        Matcher matcher = viaPattern.matcher(article);
        if (matcher.find()) {
            String source = matcher.group(1).trim();
            // Additional cleaning
            if (source.startsWith("AP")) source = "AP News";
            if (source.startsWith("BBC")) source = "BBC";
            if (source.startsWith("CNN")) source = "CNN";
            return source;
        }
        return "WorldNews";
    }

    /**
     * Extracts headline from article string.
     * @param article Raw article string.
     * @return Headline text.
     */
    private static String extractHeadline(String article) {
        Pattern titlePattern = Pattern.compile("Title: ([^|]+)");
        Matcher matcher = titlePattern.matcher(article);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Breaking news";
    }
}