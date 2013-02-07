package com.lopefied.pepemon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.lopefied.pepemon.R;

public class FacebookImageLoader {
    public static final String TAG = FacebookImageLoader.class.getSimpleName();
    private final int stub_id = R.drawable.ic_blank_picture_inverse;
    private MemoryCache memoryCache = new MemoryCache();
    private static FileCache fileCache;
    private static String accessToken;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    protected ProgressBar progressBar;
    private static FacebookImageLoader INSTANCE = new FacebookImageLoader();

    private FacebookImageLoader() {
        executorService = Executors.newFixedThreadPool(5);
    }

    public static FacebookImageLoader getInstance(Context context,
            String accessToken) {
        if (fileCache == null)
            fileCache = new FileCache(context);
        FacebookImageLoader.accessToken = accessToken;
        return INSTANCE;
    }

    public void displayImage(String photoID, ImageView imageView) {
        imageViews.put(imageView, photoID);
        Bitmap bitmap = memoryCache.get(photoID);
        if (bitmap != null) {
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(photoID, imageView);
            imageView.setScaleType(ScaleType.CENTER_INSIDE);
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String photoID, ImageView imageView) {
        FacebookPhotoToLoad p = new FacebookPhotoToLoad(photoID, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    public String getPhotoURL(String photoID) {
        Log.i(TAG, "Getting photo url of photo ID : " + photoID);
        return FBUtil.extractImageURLFromPID(photoID, accessToken);
    }

    public Bitmap getBitmap(String photoID) {
        System.out.println("Downloading image with photoID : " + photoID);
        File f = fileCache.getFile(photoID);

        // from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        // from web
        HttpURLConnection conn = null;
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(getPhotoURL(photoID));
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            System.out.println("-------disconnecting!!!");
            if (conn != null)
                conn.disconnect();
        }
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 300;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class FacebookPhotoToLoad {
        public String photoID;
        public ImageView imageView;

        public FacebookPhotoToLoad(String u, ImageView i) {
            photoID = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        FacebookPhotoToLoad photoToLoad;

        PhotosLoader(FacebookPhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.photoID);
            memoryCache.put(photoToLoad.photoID, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(FacebookPhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.photoID))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        FacebookPhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, FacebookPhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setVisibility(View.VISIBLE);
                photoToLoad.imageView.setImageBitmap(bitmap);
                photoToLoad.imageView.setScaleType(ScaleType.CENTER_CROP);
            } else {
                photoToLoad.imageView.setVisibility(View.VISIBLE);
                photoToLoad.imageView.setImageResource(stub_id);
                photoToLoad.imageView.setScaleType(ScaleType.CENTER_INSIDE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
    }

}
