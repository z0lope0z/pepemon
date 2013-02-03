package com.lopefied.pepemon.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
@DatabaseTable(tableName = "album")
public class Album {
    public static final String ALBUM_ID = "album_id";
    @DatabaseField(generatedId = true)
    private Integer ID;
    @DatabaseField(columnName = ALBUM_ID)
    private String albumID;
    @DatabaseField(columnName = "photo_count")
    private String albumPhotoCount;
    @DatabaseField(columnName = "name")
    private String albumName;
    @DatabaseField(columnName = "cover_url")
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

    public Integer getID() {
        return ID;
    }

    public void setID(Integer iD) {
        ID = iD;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((albumID == null) ? 0 : albumID.hashCode());
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
        Album other = (Album) obj;
        if (albumID == null) {
            if (other.albumID != null)
                return false;
        } else if (!albumID.equals(other.albumID))
            return false;
        return true;
    }

}
