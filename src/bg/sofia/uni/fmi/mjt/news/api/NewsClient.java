package bg.sofia.uni.fmi.mjt.news.api;

import bg.sofia.uni.fmi.mjt.news.data.NewsRequest;
import bg.sofia.uni.fmi.mjt.news.data.Page;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsClientException;

import java.util.List;

public interface NewsClient {

    Page searchByPage(NewsRequest request) throws NewsClientException;

    List<Page> searchAll(NewsRequest request) throws NewsClientException;
}