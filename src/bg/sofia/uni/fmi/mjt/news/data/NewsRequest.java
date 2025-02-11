package bg.sofia.uni.fmi.mjt.news.data;

import java.util.List;

public class NewsRequest {

    private final List<String> keywords;
    private final String category;
    private final String country;
    private final int page;
    private final int pageSize;

    private NewsRequest(Builder builder) {
        this.keywords = builder.keywords;
        this.category = builder.category;
        this.country = builder.country;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public NewsRequest.Builder toBuilder() {
        Builder builder = new Builder(this.keywords);

        if (this.category != null) {
            builder.category(this.category);
        }

        if (this.country != null) {
            builder.country(this.country);
        }

        if (this.page != -1) {
            builder.page(this.page);
        }

        if (this.pageSize != Builder.DEFAULT_PAGE_SIZE) {
            builder.pageSize(this.pageSize);
        }

        return builder;
    }

    public static Builder builder(List<String> keyword) {
        return new Builder(keyword);
    }

    public static class Builder {
        private static final int DEFAULT_PAGE_SIZE = 10;

        private final List<String> keywords;
        private String category;
        private String country;
        private int page = -1;
        private int pageSize = DEFAULT_PAGE_SIZE;

        public Builder(List<String> keywords) {
            if (keywords == null || keywords.isEmpty()) {
                throw new IllegalArgumentException("Keywords cannot be null or empty");
            }
            this.keywords = keywords;
        }

        public Builder category(String category) {
            if (category == null || category.isEmpty()) {
                throw new IllegalArgumentException("Category cannot be null or empty");
            }
            this.category = category;
            return this;
        }

        public Builder country(String country) {
            if (country == null || country.isEmpty()) {
                throw new IllegalArgumentException("Country cannot be null or empty");
            }
            this.country = country;
            return this;
        }

        public Builder page(int page) {
            if (page < 1) {
                throw new IllegalArgumentException("Page must be greater than 0");
            }
            this.page = page;
            return this;
        }

        public Builder pageSize(int pageSize) {
            if (pageSize < 1) {
                throw new IllegalArgumentException("Page size must be greater than 0");
            }
            this.pageSize = pageSize;
            return this;
        }

        public NewsRequest build() {
            return new NewsRequest(this);
        }
    }
}
