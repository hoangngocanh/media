package com.example.ngocanhpro.media.enity;

import java.util.ArrayList;

public class Artist {
    long id;
    String name;
    int numTrack;
    int numAlbum;
    private long albumID;
    ArrayList<Album> listAlbum = new ArrayList<>();

    public Artist(long id, String name, int numTrack, int numAlbum, ArrayList<Album> listAlbum) {
        this.id = id;
        this.name = name;
        this.numTrack = numTrack;
        this.numAlbum = numAlbum;
        this.listAlbum = listAlbum;
    }

    public Artist(long id, String name, int numTrack, int numAlbum, long albumID) {
        this.id = id;
        this.name = name;
        this.numTrack = numTrack;
        this.numAlbum = numAlbum;
        this.albumID = albumID;
    }

    public Artist(long id, String name, int numTrack, int numAlbum) {
        this.id = id;
        this.name = name;
        this.numTrack = numTrack;
        this.numAlbum = numAlbum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumTrack() {
        return numTrack;
    }

    public void setNumTrack(int numTrack) {
        this.numTrack = numTrack;
    }

    public int getNumAlbum() {
        return numAlbum;
    }

    public void setNumAlbum(int numAlbum) {
        this.numAlbum = numAlbum;
    }

    public long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public ArrayList<Album> getListAlbum() {
        return listAlbum;
    }

    public void setListAlbum(ArrayList<Album> listAlbum) {
        this.listAlbum = listAlbum;
    }
}
