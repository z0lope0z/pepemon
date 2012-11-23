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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((photoID == null) ? 0 : photoID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Photo other = (Photo) obj;
        if (photoID == null) {
            if (other.photoID != null)
                return false;
        } else if (!photoID.equals(other.photoID))
            return false;
        return true;
    }

}
