package com.lopefied.pepemon.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lopefied.pepemon.R;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.util.ImageLoader;

/**
 * @author Lope Chupijay Emano
 */
public class PhotoListAdapter extends ArrayAdapter<Photo> {
    public static final String TAG = PhotoListAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private List<Photo> photoList = new ArrayList<Photo>();
    private Context mContext;
    private IPhotoListAdapter photoListAdapterListener;

    public PhotoListAdapter(Context context, int textViewResourceId,
            List<Photo> photoList, IPhotoListAdapter photoListAdapterListener) {
        super(context, textViewResourceId, photoList);
        this.mContext = context;
        this.photoList = photoList;
        this.imageLoader = ImageLoader.getInstance(context);
        this.photoListAdapterListener = photoListAdapterListener;
    }

    public void clearCache() {
        System.gc();
        Log.i(TAG, "Clearing cache");
        imageLoader.clearCache();
    }

    @Override
    public void clear() {
        photoList.clear();
    }

    public void clearImageCache() {
        imageLoader.clearCache();
    }

    public void addAll(Collection<? extends Photo> collection) {
        photoList.addAll(collection);
    }

    public void set(Collection<? extends Photo> collection) {
        photoList.clear();
        photoList.addAll(collection);
    }

    public int getCount() {
        return this.photoList.size();
    }

    public Photo getItem(int index) {
        return this.photoList.get(index);
    }

    public List<Photo> getList() {
        return this.photoList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_album, parent, false);
            // View holder for smooth scrolling
            holder = new ViewHolder();
            holder.lblTitle = (TextView) row.findViewById(R.id.lblTitle);
            holder.imgProductThumbnail = (ImageView) row
                    .findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            final Photo photo = getItem(position);
            holder = (ViewHolder) row.getTag();
            imageLoader.displayImage(photo.getPhotoURL(),
                    holder.imgProductThumbnail);
            holder.imgProductThumbnail
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photoListAdapterListener.selectPhoto(photo);
                        }
                    });
            holder.lblTitle.setVisibility(View.GONE);
        }
        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public interface IPhotoListAdapter {
        public String getFBToken();

        public void selectPhoto(Photo photo);
    }

    static class ViewHolder {
        public TextView lblTitle;
        public ImageView imgProductThumbnail;
    }

}
