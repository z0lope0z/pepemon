package com.lopefied.pepemon.provider.impl;

import java.util.List;

import android.app.ProgressDialog;
import android.util.Log;

import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.provider.AlbumPhotosListener;
import com.lopefied.pepemon.provider.AlbumPhotosProvider;
import com.lopefied.pepemon.service.PhotoService;
import com.lopefied.pepemon.task.GetAlbumPhotosTask;
import com.lopefied.pepemon.task.GetAlbumPhotosTask.IAlbumPhotosDownloader;

public class AlbumPhotosProviderImpl implements AlbumPhotosProvider {
    public static final String TAG = AlbumPhotosProvider.class.getSimpleName();
    private static final Integer LIMIT = 8;

    private PhotoService photoService;
    private ProgressDialog progressDialog;
    private String accessToken;
    private Boolean isDownloading;

    public AlbumPhotosProviderImpl(PhotoService photoService,
            ProgressDialog progressDialog, String accessToken) {
        this.photoService = photoService;
        this.progressDialog = progressDialog;
        this.accessToken = accessToken;
        this.isDownloading = false;
    }

    @Override
    public void loadInit(AlbumPhotosListener albumPhotosListener, Album album) {
        List<Photo> cacheList = photoService.getAlbumPhotos(album, LIMIT);
        if (cacheList.size() > 0) {
            albumPhotosListener.addNewPhotos(cacheList);
        } else {
            loadFromServer(albumPhotosListener, album, 0);
        }
    }

    @Override
    public void loadMore(final AlbumPhotosListener albumPhotosListener,
            Photo lastPhoto, Album album, Integer currentPage) {
        Photo lastPhotoCache = photoService.getLastPhoto(album);
        if (lastPhotoCache != null) {
            if (lastPhoto == null) {
                System.out.println("last photo was null");
                loadInit(albumPhotosListener, album);
            } else if (!lastPhoto.equals(lastPhotoCache)) {
                albumPhotosListener.addNewPhotos(loadFromCache(
                        albumPhotosListener, lastPhoto, album));
            } else {
                loadFromServer(albumPhotosListener, album, currentPage);
            }
        } else
            loadFromServer(albumPhotosListener, album, currentPage);
    }

    @Override
    public Boolean isDownloading() {
        return isDownloading;
    }

    private void loadFromServer(final AlbumPhotosListener albumPhotosListener,
            final Album album, final Integer currentPage) {
        Log.i(TAG, "Loading new photos from server.. ");
        IAlbumPhotosDownloader albumPhotosDownloader = new IAlbumPhotosDownloader() {
            @Override
            public void noMoreAlbumPhotos() {
                isDownloading = false;
                albumPhotosListener.noMorePhotos();
            }

            @Override
            public void foundAlbumPhotos(List<Photo> photoList) {
                isDownloading = false;
                albumPhotosListener.addNewPhotos(photoList);
            }

        };
        GetAlbumPhotosTask task = new GetAlbumPhotosTask(photoService,
                albumPhotosDownloader, progressDialog, accessToken, currentPage);
        task.execute(album);
        isDownloading = true;
    }

    private List<Photo> loadAllFromCache(Album album) {
        Log.i(TAG, "Loading all photos from cache.. ");
        return photoService.getAlbumPhotos(album);
    }

    private List<Photo> loadFromCache(
            final AlbumPhotosListener albumPhotosListener, Photo lastPhoto,
            Album album) {
        Log.i(TAG, "Loading filtered photos from cache.. ");
        return photoService.getAlbumPhotos(album, PhotoService.BACKWARDS,
                lastPhoto.getID(), LIMIT);
    }
}
