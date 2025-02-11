package bg.sofia.uni.fmi.mjt.news.uri;

import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;

import java.util.List;

public class NewsApiUriBuilder implements UriBuilder {
    private static final String API_URL = "https://newsapi.org/v2/top-headlines";
    private String apiKey;

    public NewsApiUriBuilder(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String buildURI(NewsRequest request) {
        StringBuilder urlBuilder = new StringBuilder(API_URL);

        urlBuilder.append("?q=");
        List<String> requestKeywords = request.getKeywords();
        for (int i = 0; i < requestKeywords.size(); i++) {
            urlBuilder.append(requestKeywords.get(i));
            if (i != requestKeywords.size() - 1) {
                urlBuilder.append("+");
            }
        }

        if (request.getCategory() != null)
            urlBuilder.append("&category=").append(request.getCategory());

        if (request.getCountry() != null)
            urlBuilder.append("&country=").append(request.getCountry());

        if (request.getPage() != -1)
            urlBuilder.append("&page=").append(request.getPage());

        if (request.getPageSize() != Integer.MAX_VALUE)
            urlBuilder.append("&pageSize=").append(request.getPageSize());

        urlBuilder.append("&apiKey=").append(apiKey);

        return urlBuilder.toString();
    }
}
