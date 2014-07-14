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
    public static final Integer LIMIT = 8;

    private PhotoService photoService;
    private ProgressDialog progressDialog;
    private String accessToken;
    private Integer pageCount;

    private AlbumPhotosDownloader albumPhotosDownloader;

    public AlbumPhotosProviderImpl(PhotoService photoService,
            ProgressDialog progressDialog, String accessToken) {
        this.photoService = photoService;
        this.progressDialog = progressDialog;
        this.accessToken = accessToken;
        this.albumPhotosDownloader = new AlbumPhotosDownloader();
    }

    @Override
    public void loadInit(AlbumPhotosListener albumPhotosListener, Album album) {
        List<Photo> cacheList = photoService.getAlbumPhotos(album, LIMIT);
        if (cacheList.size() > 0) {
            albumPhotosListener.addNewPhotos(cacheList);
        } else {
            AlbumPhotosDownloader albumPhotosDownloader = new AlbumPhotosDownloader(
                    albumPhotosListener, progressDialog);
            loadFromServer(albumPhotosListener, album, 0, albumPhotosDownloader);
        }
    }

    @Override
    public void loadMore(final AlbumPhotosListener albumPhotosListener,
            Photo lastPhoto, Album album, Integer totalItems) {
        Photo lastPhotoCache = photoService.getLastPhoto(album);
        albumPhotosDownloader = new AlbumPhotosDownloader(albumPhotosListener);
        if (lastPhotoCache != null) {
            if (lastPhoto == null) {
                loadInit(albumPhotosListener, album);
            } else if (!lastPhoto.equals(lastPhotoCache)) {
                albumPhotosListener.addNewPhotos(loadFromCache(
                        albumPhotosListener, lastPhoto, album));
            } else {
                loadFromServer(albumPhotosListener, album, totalItems,
                        albumPhotosDownloader);
            }
        } else
            loadFromServer(albumPhotosListener, album, totalItems,
                    albumPhotosDownloader);
    }

    @Override
    public Boolean isDownloading() {
        Log.i(TAG, "The downloader is currently : "
                + albumPhotosDownloader.isDownloading);
        return albumPhotosDownloader.isDownloading;
    }

    private void increasePageCount() {
        pageCount = pageCount + LIMIT;
    }

    private void loadFromServer(final AlbumPhotosListener albumPhotosListener,
            final Album album, final Integer currentPage,
            AlbumPhotosDownloader albumPhotosDownloader) {
        Log.i(TAG, "Loading new photos from server.. ");
        GetAlbumPhotosTask task = new GetAlbumPhotosTask(photoService,
                albumPhotosDownloader, accessToken, currentPage);
        task.execute(album);
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
                lastPhoto, LIMIT);
    }

    private class AlbumPhotosDownloader implements IAlbumPhotosDownloader {
        Boolean isDownloading;
        AlbumPhotosListener albumPhotosListener;
        ProgressDialog progressDialog;

        public AlbumPhotosDownloader() {
            this.isDownloading = false;
        }

        public AlbumPhotosDownloader(AlbumPhotosListener albumPhotosListener) {
            this.isDownloading = false;
            this.albumPhotosListener = albumPhotosListener;
        }

        public AlbumPhotosDownloader(AlbumPhotosListener albumPhotosListener,
                ProgressDialog progressDialog) {
            this.isDownloading = false;
            this.albumPhotosListener = albumPhotosListener;
            this.progressDialog = progressDialog;
        }

        @Override
        public void startingDownload() {
            this.isDownloading = true;
            albumPhotosListener.startingDownload();
            if (progressDialog != null) {
                progressDialog.show();
            }
        }

        @Override
        public void noMoreAlbumPhotos() {
            this.isDownloading = false;
            albumPhotosListener.noMorePhotos();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void foundAlbumPhotos(List<Photo> photoList) {
            this.isDownloading = false;
            albumPhotosListener.addNewPhotos(photoList);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

    }
}
