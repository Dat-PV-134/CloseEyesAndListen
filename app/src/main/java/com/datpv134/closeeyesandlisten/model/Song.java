package com.datpv134.closeeyesandlisten.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Song implements Serializable, Comparable<Song> {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("playList")
    @Expose
    private String playList;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("src")
    @Expose
    private String src;

    public Song(String id, String name, String author, String playList, String image, String src) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.playList = playList;
        this.image = image;
        this.src = src;
    }

    public Song() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPlayList() {
        return playList;
    }

    public void setPlayList(String playList) {
        this.playList = playList;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public int compareTo(Song song) {
        int aSong = Integer.parseInt(id);
        int bSong = Integer.parseInt(song.getId());
        return aSong - bSong;
    }
}
