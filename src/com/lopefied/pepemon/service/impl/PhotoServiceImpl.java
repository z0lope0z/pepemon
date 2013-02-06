package com.lopefied.pepemon.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.service.PhotoService;
import com.lopefied.pepemon.service.exception.NoPhotosExistException;
import com.lopefied.pepemon.util.FBParseUtils;

public class PhotoServiceImpl implements PhotoService {
    private Dao<Photo, Integer> photoDAO;

    public PhotoServiceImpl(Dao<Photo, Integer> photoDAO) {
        this.photoDAO = photoDAO;
    }

    @Override
    public List<Photo> processJSONArrayResponse(String response,
            String accessToken, Album album) throws NoPhotosExistException,
            JSONException {
        List<Photo> photoList = new ArrayList<Photo>();
        JSONObject JOTemp = new JSONObject(response);

        JSONArray JAAlbumPhotos = JOTemp.getJSONArray("data");

        if (JAAlbumPhotos.length() == 0) {
            throw new NoPhotosExistException(album.getAlbumID());
        } else {
            // PAGING JSONOBJECT
            Photo photo;
            for (int i = 0; i < JAAlbumPhotos.length(); i++) {
                JSONObject JOPhoto = JAAlbumPhotos.getJSONObject(i);
                photo = new Photo();
                // GET THE ALBUM ID
                if (JOPhoto.has("pid")) {
                    photo.setPhotoID(JOPhoto.getString("pid"));
                } else {
                    photo.setPhotoID(null);
                }
                String returnImageURL = FBParseUtils
                        .extractURLFromImageObject(JOPhoto);
                photo.setPhotoURL(returnImageURL);
                photo.setAlbum(album);
                try {
                    photoDAO.createOrUpdate(photo);
                    photoList.add(photo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoList;
    }

    @Override
    public Boolean isCached() {
        try {
            return photoDAO.countOf() > Long.valueOf(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Photo> getAlbumPhotos(Album album) {
        try {
            QueryBuilder<Photo, Integer> queryBuilder = photoDAO.queryBuilder();
            queryBuilder.where().eq(Photo.ALBUM, album);
            PreparedQuery<Photo> prepQuery = queryBuilder.orderBy(Photo.ID_PK,
                    true).prepare();
            return photoDAO.query(prepQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<Photo>();
    }

    @Override
    public List<Photo> getAlbumPhotos(Album album, Integer limit) {
        try {
            QueryBuilder<Photo, Integer> queryBuilder = photoDAO.queryBuilder();
            queryBuilder.where().eq(Photo.ALBUM, album);
            PreparedQuery<Photo> prepQuery = queryBuilder
                    .orderBy(Photo.ID_PK, true).limit(Long.valueOf(limit))
                    .prepare();
            return photoDAO.query(prepQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<Photo>();
    }

    @Override
    public List<Photo> getAlbumPhotos(Album album, int direction,
            Photo photo, Integer limit) {
        List<Photo> result = new ArrayList<Photo>();
        try {
            QueryBuilder<Photo, Integer> queryBuilder = photoDAO.queryBuilder();
            if (direction == PhotoService.BACKWARDS)
                queryBuilder.where().gt(Photo.ID_PK, photo.getID()).and()
                        .eq(Photo.ALBUM, album);
            else if (direction == PhotoService.FORWARDS)
                queryBuilder.where().lt(Photo.ID_PK, photo.getID()).and()
                        .eq(Photo.ALBUM, album);
            else
                throw new SQLException();
            PreparedQuery<Photo> prepQuery = queryBuilder
                    .orderBy(Photo.ID_PK, true).limit(new Long(limit))
                    .prepare();
            return photoDAO.query(prepQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Photo getLastPhoto(Album album) {
        QueryBuilder<Photo, Integer> queryBuilder = photoDAO.queryBuilder();
        try {
            queryBuilder.where().eq(Photo.ALBUM, album);
            PreparedQuery<Photo> prepQuery = queryBuilder.orderBy(Photo.ID_PK,
                    false).prepare();
            Photo result = photoDAO.queryForFirst(prepQuery);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
