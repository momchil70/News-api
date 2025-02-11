package bg.sofia.uni.fmi.mjt.news.api;

import bg.sofia.uni.fmi.mjt.news.data.Article;
import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;
import bg.sofia.uni.fmi.mjt.news.data.Page;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsClientException;
import bg.sofia.uni.fmi.mjt.news.uri.NewsApiUriBuilder;
import bg.sofia.uni.fmi.mjt.news.uri.UriBuilder;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NewsClientImplTest {

    private NewsClientImpl newsClient;
    private HttpClient mockHttpClient;
    private UriBuilder mockUriBuilder;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        mockHttpClient = mock(HttpClient.class);
        mockUriBuilder = mock(NewsApiUriBuilder.class);

        newsClient = new NewsClientImpl("fakeApiKey", mockHttpClient);
    }

    @Test
    void testSearchByPageValidWorkflow() throws Exception {
        NewsRequest request = NewsRequest.builder(List.of("keyword"))
            .category("category")
            .country("country")
            .page(1)
            .pageSize(10)
            .build();

        String jsonResponse = "{ \"status\": \"ok\", \"articles\": [] }";
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.body()).thenReturn(jsonResponse);
        when(responseMock.statusCode()).thenReturn(200);

        when(mockUriBuilder.buildURI(request)).thenReturn("https://test-uri.com");

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(responseMock);

        Page page = newsClient.searchByPage(request);

        assertNotNull(page, "The page should not be null");
        assertEquals(1, page.pageNumber(), "The page number should be 1");
        assertEquals(0, page.content().size(), "The articles list should be empty");
    }

    @Test
    void testSearchByPagePageNotSet() {
        NewsRequest request = Mockito.mock(NewsRequest.class);
        when(request.getPage()).thenReturn(-1);
        assertThrows(IllegalArgumentException.class, () -> newsClient.searchByPage(request),
            "Page must be set in searchByPage!");
    }

    @Test
    void testSearchByPageNullRequest() {

        assertThrows(IllegalArgumentException.class, () -> newsClient.searchByPage(null),
            "Page cannot be null in searchByPage!");
    }

    @Test
    void testSearchByPageApiError() throws Exception {
        NewsRequest request = NewsRequest.builder(List.of("keyword"))
            .category("category")
            .country("country")
            .page(1)
            .pageSize(10)
            .build();

        String errorResponse = "{\"message\": \"Internal Server Error\", \"code\": \"500\"}";
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.body()).thenReturn(errorResponse);
        when(responseMock.statusCode()).thenReturn(500);

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(responseMock);

        NewsClientException exception = assertThrows(NewsClientException.class, () ->
            newsClient.searchByPage(request),
            "Should throw NewsClientException when statusCode != 200");
    }

    @Test
    void testSearchByPageWithErrorInResponseBody() throws Exception {
        NewsRequest request = NewsRequest.builder(List.of("keyword"))
            .category("category")
            .country("country")
            .page(1)
            .pageSize(10)
            .build();

        String errorResponse = "{ \"status\": \"error\", \"message\": \"Invalid API key\" }";
        HttpResponse<String> responseMock = Mockito.mock(HttpResponse.class);
        when(responseMock.body()).thenReturn(errorResponse);
        when(responseMock.statusCode()).thenReturn(200);

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(responseMock);

        assertThrows(NewsClientException.class, () -> newsClient.searchByPage(request),
            "Should throw NewsClientException when the status is error!");
    }

    @Test
    void testSearchAllWithMultiplePages() throws Exception {
        NewsRequest request = NewsRequest.builder(List.of("keyword"))
            .category("category")
            .country("country")
            .pageSize(2)
            .build();

        List<Article> allArticles = List.of(
            new Article("Title1", "Description1", "Url1", "Source1", "date1"),
            new Article("Title2", "Description2", "Url2", "Source2", "date2"),
            new Article("Title3", "Description3", "Url3", "Source3", "date3"),
            new Article("Title4", "Description4", "Url4", "Source4", "date4")
        );

        String jsonResponse = "{ \"status\": \"ok\", \"articles\": " + gson.toJson(allArticles) + " }";
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.body()).thenReturn(jsonResponse);
        when(responseMock.statusCode()).thenReturn(200);

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(responseMock);

        List<Page> pages = newsClient.searchAll(request);

        assertNotNull(pages, "The result should not be null");
        assertEquals(2, pages.size(), "There should be 2 pages");
        assertEquals(2, pages.get(0).content().size(),
            "First page should contain 2 articles");
        assertEquals(2, pages.get(1).content().size(),
            "Second page should contain 2 articles");

        assertEquals("Title1", pages.get(0).content().get(0).getTitle(),
            "Expected title1 but was " + pages.get(0).content().get(0).getTitle());
        assertEquals("Title3", pages.get(1).content().get(0).getTitle(),
            "Expected title3 but was " + pages.get(1).content().get(0).getTitle());
    }

    @Test
    void testSearchAllWithEmptyResult() throws Exception {
        NewsRequest request = NewsRequest.builder(List.of("keyword"))
            .category("category")
            .country("country")
            .pageSize(5)
            .build();

        String jsonResponse = "{ \"status\": \"ok\", \"articles\": [] }";
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.body()).thenReturn(jsonResponse);
        when(responseMock.statusCode()).thenReturn(200);

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(responseMock);

        List<Page> pages = newsClient.searchAll(request);

        assertNotNull(pages, "The result should not be null");
        assertEquals(0, pages.size(), "The result should contain 0 pages");
    }

    @Test
    void testSearchAllWithBadStatusCode() throws Exception {
        NewsRequest request = NewsRequest.builder(List.of("keyword"))
            .category("category")
            .country("country")
            .pageSize(5)
            .build();

        String errorResponse = "{\"message\": \"Invalid API key\", \"status\": \"error\"}";
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.body()).thenReturn(errorResponse);
        when(responseMock.statusCode()).thenReturn(500);

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(responseMock);

        assertThrows(NewsClientException.class, () -> newsClient.searchAll(request),
            "Should throw an NewsClientException when statusCode != 200");
    }
}
