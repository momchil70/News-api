package bg.sofia.uni.fmi.mjt.news.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NewsRequestTest {

    @Test
    public void testBuilderWithAllFields() {
        NewsRequest request = new NewsRequest.Builder(Arrays.asList("keyword1", "keyword2"))
            .category("category")
            .country("country")
            .page(1)
            .pageSize(20)
            .build();

        assertNotNull(request, "NewsRequest should not be null");
        assertEquals(2, request.getKeywords().size(), "Keywords size should be 2");
        assertEquals("category", request.getCategory(), "Category should be 'category'");
        assertEquals("country", request.getCountry(), "Country should be 'country'");
        assertEquals(1, request.getPage(), "Page should be 1");
        assertEquals(20, request.getPageSize(), "Page size should be 20");
    }

    @Test
    public void testBuilderWithDefaultPageSize() {
        NewsRequest request = new NewsRequest.Builder(Arrays.asList("keyword1"))
            .build();

        assertNotNull(request, "NewsRequest should not be null");
        assertEquals(1, request.getKeywords().size(), "Keywords size should be 1");
        assertEquals(-1, request.getPage(), "Page should be -1");
        assertEquals(10, request.getPageSize(), "Default page size should be 10");
    }

    @Test
    public void testBuilderWithInvalidKeywords() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(null).build();
        }, "Keywords cannot be null or empty");

        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList()).build();
        }, "Keywords cannot be null or empty");
    }

    @Test
    public void testBuilderWithInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .category(null).build();
        }, "Category cannot be null or empty");

        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .category("").build();
        }, "Category cannot be null or empty");
    }

    @Test
    public void testBuilderWithInvalidCountry() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .country(null).build();
        }, "Country cannot be null or empty");

        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .country("").build();
        }, "Country cannot be null or empty");
    }

    @Test
    public void testBuilderWithInvalidPage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .page(0).build();
        }, "Page must be greater than 0");

        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .page(-1).build();
        }, "Page must be greater than 0");
    }

    @Test
    public void testBuilderWithInvalidPageSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .pageSize(0).build();
        }, "Page size must be greater than 0");

        assertThrows(IllegalArgumentException.class, () -> {
            new NewsRequest.Builder(Arrays.asList("keyword"))
                .pageSize(-1).build();
        }, "Page size must be greater than 0");
    }

    @Test
    public void testToBuilder() {
        NewsRequest original = new NewsRequest.Builder(Arrays.asList("keyword1"))
            .category("category")
            .country("country")
            .page(2)
            .pageSize(15)
            .build();

        NewsRequest copy = original.toBuilder().build();

        assertEquals(original.getKeywords(), copy.getKeywords(), "Keywords should match");
        assertEquals(original.getCategory(), copy.getCategory(), "Category should match");
        assertEquals(original.getCountry(), copy.getCountry(), "Country should match");
        assertEquals(original.getPage(), copy.getPage(), "Page should match");
        assertEquals(original.getPageSize(), copy.getPageSize(), "Page size should match");
    }
}
