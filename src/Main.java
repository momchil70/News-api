package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.api.NewsClient;
import bg.sofia.uni.fmi.mjt.news.api.NewsClientImpl;
import bg.sofia.uni.fmi.mjt.news.data.Article;
import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;
import bg.sofia.uni.fmi.mjt.news.data.Page;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsClientException;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String apiKey = "c304bb58b8784522805f4cf262c8b84f";
        HttpClient client = HttpClient.newHttpClient();

        NewsClient newsClient = new NewsClientImpl(apiKey, client);

        List<String> words = new ArrayList<>();
        words.add("facebook");

        NewsRequest request = NewsRequest.builder(words)
            .pageSize(3)
            .build();

        try {
            List<Page> p = newsClient.searchAll(request);

            for (Page page : p) {
                for (Article a: page.content()) {
                    System.out.println(a);
                }
                System.out.println();
                System.out.println();
                System.out.println();
            }

            //if (p.content().isEmpty()) System.out.println("Empty");
            //p.content().forEach(System.out::println);
        } catch (NewsClientException e) {
            System.out.println("Error fetching news: " + e.getMessage());
        }
    }
}
