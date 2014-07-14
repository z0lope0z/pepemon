package com.lopefied.pepemon.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.Util;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.service.AlbumService;
import com.lopefied.pepemon.task.GetAlbumsTask.IAlbumDownloader;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class GetAlbumsFQLTask extends AsyncTask<String, Void, List<Album>> {
    public static final String TAG = GetAlbumsFQLTask.class.getSimpleName();
    public static final String PEPEMON_ID = "pepemon2";

    private ProgressDialog progressDialog;
    private IAlbumDownloader albumDownloader;
    private Boolean stopLoadingData;
    private Boolean loadingMore;
    private String accessToken;
    private AlbumService albumService;

    public GetAlbumsFQLTask(AlbumService albumService,
            IAlbumDownloader albumDownloader, ProgressDialog progressDialog,
            String accessToken) {
        this.albumDownloader = albumDownloader;
        this.progressDialog = progressDialog;
        this.accessToken = accessToken;
        this.albumService = albumService;
    }

    @Override
    protected void onPreExecute() {
        // SHOW THE PROGRESS BAR (SPINNER) WHILE LOADING ALBUMS
        progressDialog.show();
    }

    @Override
    protected List<Album> doInBackground(String... params) {
        List<Album> albumList = new ArrayList<Album>();

        // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
        // MORE DATA WHILE LOADING A BATCH
        loadingMore = true;

        // SET THE INITIAL URL TO GET THE FIRST LOT OF ALBUMS
        String FACEBOOK_ID = params[0];
        try {
            String queryAlbums = "";

            String query = "SELECT aid, name, photo_count, cover_pid FROM album WHERE owner IN (SELECT page_id from page where username=\""
                    + FACEBOOK_ID + "\")";
            Bundle b = new Bundle();
            b.putString("access_token", accessToken);
            b.putString("q", query);

            try {
                queryAlbums = Util.openUrl("https://graph.facebook.com/fql",
                        "GET", b);
                Log.i(TAG, queryAlbums);
                albumList = albumService.processJSONArrayResponse(queryAlbums,
                        accessToken, PEPEMON_ID);
                Log.i(TAG, "Returning album list with size : " + queryAlbums);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumList;
    }

    @Override
    protected void onPostExecute(List<Album> albumList) {
        // // SET THE ADAPTER TO THE LISTVIEW
        // lv.setAdapter(adapter);
        // CHANGE THE LOADING MORE STATUS
        loadingMore = false;
        // HIDE THE PROGRESS BAR (SPINNER) AFTER LOADING ALBUMS
        progressDialog.hide();
        albumDownloader.foundAlbums(albumList);
        albumDownloader.noMoreAlbums();
    }

}