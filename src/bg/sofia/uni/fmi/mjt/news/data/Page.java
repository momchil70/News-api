package bg.sofia.uni.fmi.mjt.news.data;

import java.util.List;

public record Page(int pageNumber, List<Article> content) {
}
