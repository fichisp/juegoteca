package com.juegoteca.tgdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private Integer id;
    private String gameTitle;
    private String releaseDate;
    private Integer platform;
    private Object developers;
    private List<Integer> genres = null;
    private Object publishers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Object getDevelopers() {
        return developers;
    }

    public void setDevelopers(Object developers) {
        this.developers = developers;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public Object getPublishers() {
        return publishers;
    }

    public void setPublishers(Object publishers) {
        this.publishers = publishers;
    }
}
