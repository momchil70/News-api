package bg.sofia.uni.fmi.mjt.news.data;

public class Article {

    private final String title;
    private final String description;
    private final String url;
    private final String publishedAt;

    public Article(String title, String description, String country, String url, String publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "Article{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", url='" + url + '\'' +
            ", publishedAt='" + publishedAt + '\'' +
            '}';
    }

    public String getTitle() {
        return title;
    }
}
