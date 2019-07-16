package com.example.ngocanhpro.media.enity;

public class Album {
    long id;
    String nameAlbum;
    String nameArtist;
    int numSong;

    public Album(long id, String nameAlbum, String nameArtist, int numSong) {
        this.id = id;
        this.nameAlbum = nameAlbum;
        this.nameArtist = nameArtist;
        this.numSong = numSong;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public String getNameArtist() {
        return nameArtist;
    }

    public void setNameArtist(String nameArtist) {
        this.nameArtist = nameArtist;
    }

    public int getNumSong() {
        return numSong;
    }

    public void setNumSong(int numSong) {
        this.numSong = numSong;
    }
}
