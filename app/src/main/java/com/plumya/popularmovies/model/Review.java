package com.plumya.popularmovies.model;

/**
 * Created by miltomasz on 05/05/17.
 */

public class Review {

    private String id;
    private long movieId;
    private String author;
    private String content;
    private String url;

    public Review(String id, long movieId, String author, String content, String url) {
        this.id = id;
        this.movieId = movieId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public long getMovieId() {
        return movieId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
