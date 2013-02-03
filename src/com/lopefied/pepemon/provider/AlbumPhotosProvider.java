package com.lopefied.pepemon.provider;

import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;

public interface AlbumPhotosProvider {
    public void loadMore(AlbumPhotosListener albumPhotosListener,
            Photo lastPhoto, Album albumID, Integer limit, Integer currentPage);

    public Boolean isDownloading();
}