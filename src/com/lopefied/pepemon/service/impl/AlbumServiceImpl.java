package com.lopefied.pepemon.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.service.AlbumService;
import com.lopefied.pepemon.service.exception.NoAlbumExistsException;
import com.lopefied.pepemon.util.FBUtil;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class AlbumServiceImpl implements AlbumService {
    private Dao<Album, Integer> albumDAO;

    public AlbumServiceImpl(Dao<Album, Integer> albumDAO) {
        this.albumDAO = albumDAO;
    }

    @Override
    public List<Album> processJSONArrayResponse(String response,
            String accessToken, String facebookID)
            throws NoAlbumExistsException, JSONException {
        List<Album> albumList = new ArrayList<Album>();
        JSONObject JOTemp = new JSONObject(response);

        JSONArray JAAlbums = JOTemp.getJSONArray("data");

        if (JAAlbums.length() == 0) {
            throw new NoAlbumExistsException(facebookID);
        } else {
            Album album;

            for (int i = 0; i < JAAlbums.length(); i++) {
                JSONObject JOAlbums = JAAlbums.getJSONObject(i);

                if (JOAlbums.has("aid")) {
                    album = new Album();
                    // GET THE ALBUM ID
                    album.setAlbumID(JOAlbums.getString("aid"));

                    // GET THE ALBUM NAME
                    if (JOAlbums.has("name")) {
                        album.setAlbumName(JOAlbums.getString("name"));
                    } else {
                        album.setAlbumName(null);
                    }

                    // GET THE ALBUM COVER PHOTO
                    if (JOAlbums.has("cover_pid")) {
                        album.setAlbumCover(FBUtil.extractImageURLFromPID(
                                JOAlbums.getString("cover_pid"), accessToken));
                    }
                    // GET THE ALBUM'S PHOTO COUNT
                    if (JOAlbums.has("photo_count")) {
                        album.setAlbumPhotoCount(JOAlbums
                                .getString("photo_count"));
                    } else {
                        album.setAlbumPhotoCount("0");
                    }
                    try {
                        albumDAO.createOrUpdate(album);
                        albumList.add(album);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return albumList;
    }

    @Override
    public Boolean isCached() {
        try {
            if (albumDAO.countOf() > new Long(0))
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Album> getAlbums() {
        try {
            return albumDAO.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<Album>();
    }
}
