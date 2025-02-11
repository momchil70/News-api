package bg.sofia.uni.fmi.mjt.news.api;

import bg.sofia.uni.fmi.mjt.news.data.Article;
import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;
import bg.sofia.uni.fmi.mjt.news.data.Page;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsClientException;
import bg.sofia.uni.fmi.mjt.news.uri.NewsApiUriBuilder;
import bg.sofia.uni.fmi.mjt.news.uri.UriBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.net.http.HttpRequest;
import java.util.Map;

public class NewsClientImpl implements NewsClient {

    private static final int OK_CODE = 200;
    private final Gson gson;
    private UriBuilder uriBuilder;
    private HttpClient client;

    public NewsClientImpl(String apiKey, HttpClient httpClient) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        uriBuilder = new NewsApiUriBuilder(apiKey);
        this.gson = new Gson();
        client = httpClient;
    }

    @Override
    public List<Page> searchAll(NewsRequest request) throws NewsClientException {
        try {
            NewsRequest updatedRequest = request.toBuilder()
                .pageSize(Integer.MAX_VALUE).page(1).build();
            Page allArticlesPage = searchByPage(updatedRequest);

            List<Article> allArticles = allArticlesPage.content();
            List<Page> result = new ArrayList<>();
            int totalArticles = allArticles.size();
            int pageSize = request.getPageSize();

            for (int i = 0; i < totalArticles; i += pageSize) {
                int end = Math.min(i + pageSize, totalArticles);
                List<Article> pageContent = allArticles.subList(i, end);
                result.add(new Page(i / pageSize + 1, pageContent));
            }

            return result;
        } catch (NewsClientException e) {
            throw new NewsClientException("Failed to fetch all articles", e);
        }
    }

    @Override
    public Page searchByPage(NewsRequest request) throws NewsClientException {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getPage() == -1) {
            throw new IllegalArgumentException("Page must be set in searchByPage!");
        }

        try {
            HttpResponse<String> response = sendRequest(request);

            if (response.statusCode() != OK_CODE) {
                handleErrorResponse(response);
            }

            return new Page(request.getPage(), getResult(response.body()));
        } catch (URISyntaxException | IOException e) {
            throw new NewsClientException("An error occurred while making the request", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> sendRequest(NewsRequest request) throws URISyntaxException,
                    IOException, InterruptedException {

        URI uri = new URI(uriBuilder.buildURI(request));
        HttpRequest hr = HttpRequest.newBuilder().uri(uri).build();
        return client.send(hr, HttpResponse.BodyHandlers.ofString());
    }

    private void handleErrorResponse(HttpResponse<String> response) throws NewsClientException {
        String responseBody = response.body();
        Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);

        String message = responseMap.containsKey("message") ?
            (String) responseMap.get("message") : "No message provided";
        throw new NewsClientException("HTTP Error: " + response.statusCode() + " - " + message);
    }

    private List<Article> getResult(String jsonResponse) throws NewsClientException {
        Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);

        if (responseMap.get("status").equals("error")) {
            throw new NewsClientException(responseMap.get("message").toString());
        }

        String articlesJson = gson.toJson(responseMap.get("articles"));
        Type articleListType = new TypeToken<List<Article>>() { }.getType();
        return gson.fromJson(articlesJson, articleListType);
    }
}