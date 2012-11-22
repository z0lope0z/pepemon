package com.lopefied.pepemon.model;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class Photo {
    private String photoURL;
    private String photoID;

    public Photo() {
    }

    public Photo(String photoURL, String photoID) {
        super();
        this.photoURL = photoURL;
        this.photoID = photoID;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}
