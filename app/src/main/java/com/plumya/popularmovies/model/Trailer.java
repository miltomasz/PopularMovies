package com.plumya.popularmovies.model;

/**
 * Created by miltomasz on 03/05/17.
 */

public class Trailer {

    private String id;
    private long movieId;
    private String key;
    private String name;

    public Trailer(String id, long movieId, String key, String name) {
        this.id = id;
        this.movieId = movieId;
        this.key = key;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
