package com.lopefied.pepemon.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.Util;
import com.lopefied.pepemon.model.Album;
import com.lopefied.pepemon.task.GetAlbumsTask.IAlbumDownloader;
import com.lopefied.pepemon.util.FBUtil;

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

    public GetAlbumsFQLTask(IAlbumDownloader albumDownloader,
            ProgressDialog progressDialog, String accessToken) {
        this.albumDownloader = albumDownloader;
        this.progressDialog = progressDialog;
        this.accessToken = accessToken;
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
        String URL = params[0];
        try {
            String queryAlbums = "";

            String query = "SELECT aid, name, photo_count, cover_pid FROM album WHERE owner IN (SELECT page_id from page where username=\"pepemon3\")";
            Bundle b = new Bundle();
            b.putString("access_token", accessToken);
            b.putString("q", query);

            try {
                queryAlbums = Util.openUrl("https://graph.facebook.com/fql",
                        "GET", b);
                Log.i(TAG, queryAlbums);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject JOTemp = new JSONObject(queryAlbums);

            JSONArray JAAlbums = JOTemp.getJSONArray("data");

            if (JAAlbums.length() == 0) {
                stopLoadingData = true;
                albumDownloader.noMoreAlbums();

            } else {
                Album albums;

                for (int i = 0; i < JAAlbums.length(); i++) {
                    JSONObject JOAlbums = JAAlbums.getJSONObject(i);

                    if (JOAlbums.has("aid")) {
                        albums = new Album();
                        // GET THE ALBUM ID
                        albums.setAlbumID(JOAlbums.getString("aid"));

                        // GET THE ALBUM NAME
                        if (JOAlbums.has("name")) {
                            albums.setAlbumName(JOAlbums.getString("name"));
                        } else {
                            albums.setAlbumName(null);
                        }

                        // GET THE ALBUM COVER PHOTO
                        if (JOAlbums.has("cover_pid")) {
                            albums.setAlbumCover(FBUtil.extractImageURLFromPID(
                                    JOAlbums.getString("cover_pid"),
                                    accessToken));
                        }
                        // GET THE ALBUM'S PHOTO COUNT
                        if (JOAlbums.has("photo_count")) {
                            albums.setAlbumPhotoCount(JOAlbums
                                    .getString("photo_count"));
                        } else {
                            albums.setAlbumPhotoCount("0");
                        }
                        albumList.add(albums);
                    }
                }
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