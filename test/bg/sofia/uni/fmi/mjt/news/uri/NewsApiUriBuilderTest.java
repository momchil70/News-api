package bg.sofia.uni.fmi.mjt.news.uri;

import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewsApiUriBuilderTest {

    private NewsApiUriBuilder uriBuilder;

    @BeforeEach
    public void setUp() {
        uriBuilder = new NewsApiUriBuilder("dummyApiKey");
    }

    @Test
    public void testBuildURI_withOnlyKeywords() {
        List<String> keywords = Arrays.asList("tech", "science");
        NewsRequest request = NewsRequest.builder(keywords).page(1).pageSize(10).build();

        String expectedURI = "https://newsapi.org/v2/top-headlines?q=tech+science&page=1" +
            "&pageSize=10&apiKey=dummyApiKey";
        assertEquals(expectedURI, uriBuilder.buildURI(request),
            "The URI should correctly encode the keywords and pagination.");
    }

    @Test
    public void testBuildURI_withCategoryAndCountry() {
        List<String> keywords = Arrays.asList("business", "finance");
        NewsRequest request = NewsRequest.builder(keywords).category("business").country("us").page(2).pageSize(5).build();

        String expectedURI = "https://newsapi.org/v2/top-headlines?q=business+finance&" +
            "category=business&country=us&page=2&pageSize=5&apiKey=dummyApiKey";
        assertEquals(expectedURI, uriBuilder.buildURI(request),
            "The URI should include category and country as well as keywords and pagination.");
    }

    @Test
    public void testBuildURI_withEmptyCategoryAndCountry() {
        List<String> keywords = Arrays.asList("health", "medicine");
        NewsRequest request = NewsRequest.builder(keywords).page(1).pageSize(20).build();

        String expectedURI = "https://newsapi.org/v2/top-headlines?q=health+" +
            "medicine&page=1&pageSize=20&apiKey=dummyApiKey";
        assertEquals(expectedURI, uriBuilder.buildURI(request),
            "The URI should not include category or country when they're not set.");
    }
}
