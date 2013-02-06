package com.lopefied.pepemon.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.Util;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.service.PhotoService;
import com.lopefied.pepemon.service.exception.NoPhotosExistException;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class GetAlbumPhotosTask extends AsyncTask<Album, Void, List<Photo>> {
    public static final String TAG = GetAlbumPhotosTask.class.getSimpleName();
    public static final Integer PAGE_COUNT = 8;
    private IAlbumPhotosDownloader albumPhotosDownloader;
    private String accessToken;
    private Integer page;
    private PhotoService photoService;

    public GetAlbumPhotosTask(PhotoService photoService,
            IAlbumPhotosDownloader albumPhotosDownloader, String accessToken,
            Integer page) {
        this.photoService = photoService;
        this.albumPhotosDownloader = albumPhotosDownloader;
        this.accessToken = accessToken;
        this.page = page;
    }

    @Override
    protected void onPreExecute() {
        albumPhotosDownloader.startingDownload();
    }

    private String createLimit() {
        return page + "," + PAGE_COUNT;
    }

    @Override
    protected List<Photo> doInBackground(Album... albums) {
        List<Photo> albumPhotoList = new ArrayList<Photo>();
        try {
            // SET THE INITIAL URL TO GET THE FIRST LOT OF ALBUMS
            Album album = albums[0];
            String albumID = album.getAlbumID();
            String queryAlbumPhotos = "";

            String query = "SELECT pid,src_big,images FROM photo WHERE aid=\""
                    + albumID + "\" LIMIT " + createLimit();
            Log.i(TAG, "Query dump : " + query);
            Bundle b = new Bundle();
            b.putString("access_token", accessToken);
            b.putString("q", query);

            try {
                queryAlbumPhotos = Util.openUrl(
                        "https://graph.facebook.com/fql", "GET", b);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return photoService.processJSONArrayResponse(queryAlbumPhotos,
                    accessToken, album);
        } catch (NoPhotosExistException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return albumPhotoList;
    }

    @Override
    protected void onPostExecute(List<Photo> albumPhotoList) {
        if (albumPhotoList.size() > 0)
            albumPhotosDownloader.foundAlbumPhotos(albumPhotoList);
        else
            albumPhotosDownloader.noMoreAlbumPhotos();
    }

    public interface IAlbumPhotosDownloader {
        public void startingDownload();

        public void noMoreAlbumPhotos();

        public void foundAlbumPhotos(List<Photo> photoList);

    }

}