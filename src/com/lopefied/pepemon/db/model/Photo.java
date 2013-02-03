package com.lopefied.pepemon.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
@DatabaseTable(tableName = "photo")
public class Photo {
    public static final String ID_PK = "ID";
    public static final String PHOTO_ID = "photo_id";
    public static final String ALBUM = "album";
    @DatabaseField(columnName = ID_PK, generatedId = true)
    private Integer ID;
    @DatabaseField(columnName = PHOTO_ID)
    private String photoID;
    @DatabaseField(columnName = "photo_url")
    private String photoURL;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ALBUM)
    private Album album;

    public Photo() {
    }

    public Photo(String photoURL, String photoID) {
        super();
        this.photoURL = photoURL;
        this.photoID = photoID;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer iD) {
        ID = iD;
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
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
