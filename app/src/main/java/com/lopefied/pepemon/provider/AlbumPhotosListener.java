package com.lopefied.pepemon.provider;

import java.util.List;

import com.lopefied.pepemon.db.model.Photo;

public interface AlbumPhotosListener {
    public void addNewPhotos(List<Photo> albumPhotos);

    public void startingDownload();

    public void noMorePhotos();

    public void error(String message);
}
