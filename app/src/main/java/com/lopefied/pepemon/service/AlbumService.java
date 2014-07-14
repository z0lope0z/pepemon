package com.lopefied.pepemon.service;

import java.util.List;

import org.json.JSONException;

import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.service.exception.NoAlbumExistsException;

public interface AlbumService {
    /**
     * Converts a json string as a json array
     * 
     * @param response
     * @return
     */
    public List<Album> processJSONArrayResponse(String response,
            String accessToken, String facebookID)
            throws NoAlbumExistsException, JSONException;

    public Boolean isCached();

    public List<Album> getAlbums();

    public Album getAlbum(String albumID) throws NoAlbumExistsException;
}
