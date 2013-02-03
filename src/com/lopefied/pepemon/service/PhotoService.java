package com.lopefied.pepemon.service;

import java.util.List;

import org.json.JSONException;

import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.service.exception.NoPhotosExistException;

public interface PhotoService {
    public static final int BACKWARDS = 0;
    public static final int FORWARDS = 1;

    /**
     * Converts a json string as a json array
     * 
     * @param response
     * @return
     */
    public List<Photo> processJSONArrayResponse(String response,
            String accessToken, Album album) throws NoPhotosExistException,
            JSONException;

    public Boolean isCached();

    public Photo getLastPhoto(Album album);

    public List<Photo> getAlbumPhotos(Album album);

    public List<Photo> getAlbumPhotos(Album album, int direction,
            Integer photoID, Integer limit);
}
