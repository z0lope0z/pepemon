package com.lopefied.pepemon.model;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class Album {
    private String albumPhotoCount;
    private String albumID;
    private String albumName;
    private String albumCover;

    public Album() {
    }

    public Album(String albumPhotoCount, String albumID, String albumName,
            String albumCover) {
        this.albumPhotoCount = albumPhotoCount;
        this.albumID = albumID;
        this.albumName = albumName;
        this.albumCover = albumCover;
    }

    public String getAlbumPhotoCount() {
        return albumPhotoCount;
    }

    public void setAlbumPhotoCount(String albumPhotoCount) {
        this.albumPhotoCount = albumPhotoCount;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

}
