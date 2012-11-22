package com.lopefied.pepemon.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lopefied.pepemon.R;
import com.lopefied.pepemon.model.Photo;
import com.lopefied.pepemon.util.ImageLoader;

/**
 * @author Lope Chupijay Emano
 */
public class PhotoListAdapter extends ArrayAdapter<Photo> {
    public static final String TAG = PhotoListAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private List<Photo> photoList = new ArrayList<Photo>();
    private Context mContext;
    private IPhotoListAdapter albumListAdapter;

    public PhotoListAdapter(Context context, int textViewResourceId,
            List<Photo> photoList, IPhotoListAdapter albumListAdapter) {
        super(context, textViewResourceId, photoList);
        this.mContext = context;
        this.photoList = photoList;
        this.imageLoader = new ImageLoader(context);
        this.albumListAdapter = albumListAdapter;
    }

    public void clearCache() {
        imageLoader.clearCache();
    }

    public void addAll(Collection<? extends Photo> collection) {
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
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_album, parent, false);
        }
        ImageView imgProductThumbnail = (ImageView) row
                .findViewById(R.id.imageView);
        Photo photo = getItem(position);
        imageLoader.displayImage(photo.getPhotoURL(), imgProductThumbnail);
        TextView lblTitle = (TextView) row.findViewById(R.id.lblTitle);
        lblTitle.setVisibility(View.GONE);
        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public interface IPhotoListAdapter {
        public String getFBToken();
    }

}
