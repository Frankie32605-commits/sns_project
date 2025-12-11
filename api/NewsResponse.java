package api;

/** Class to map the root JSON response */
public class NewsResponse {
    public NewsArticle[] news; // The API returns the articles array under the 'news' key
}