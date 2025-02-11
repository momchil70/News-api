package bg.sofia.uni.fmi.mjt.news.uri;

import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;

public interface UriBuilder {
    String buildURI(NewsRequest request);
}
