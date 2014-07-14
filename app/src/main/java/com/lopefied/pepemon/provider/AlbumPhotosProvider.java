package com.lopefied.pepemon.provider;

import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;

public interface AlbumPhotosProvider {
    public void loadInit(AlbumPhotosListener albumPhotosListener, Album album);

    public void loadMore(AlbumPhotosListener albumPhotosListener,
            Photo lastPhoto, Album album, Integer currentPage);

    public Boolean isDownloading();
}