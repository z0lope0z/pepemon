package com.lopefied.pepemon.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lopefied.pepemon.R;
import com.lopefied.pepemon.model.Album;
import com.lopefied.pepemon.util.ImageLoader;

/**
 * @author Lope Chupijay Emano
 */
public class AlbumListAdapter extends ArrayAdapter<Album> {
    public static final String TAG = AlbumListAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private List<Album> albumList = new ArrayList<Album>();
    private Context mContext;
    private IAlbumListAdapter albumListAdapterListener;

    public AlbumListAdapter(Context context, int textViewResourceId,
            List<Album> albumList, IAlbumListAdapter albumListAdapter) {
        super(context, textViewResourceId, albumList);
        this.mContext = context;
        this.albumList = albumList;
        this.imageLoader = new ImageLoader(context);
        this.albumListAdapterListener = albumListAdapter;
    }

    public void clearCache() {
        imageLoader.clearCache();
    }

    public void addAll(Collection<? extends Album> collection) {
        albumList.addAll(collection);
    }

    public int getCount() {
        return this.albumList.size();
    }

    public Album getItem(int index) {
        return this.albumList.get(index);
    }

    public List<Album> getList() {
        return this.albumList;
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
        final Album album = getItem(position);
        imageLoader.displayImage(album.getAlbumCover(), imgProductThumbnail);
        imgProductThumbnail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                albumListAdapterListener.selected(album);
            }
        });
        TextView lblTitle = (TextView) row.findViewById(R.id.lblTitle);
        lblTitle.setText(album.getAlbumName());
        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public interface IAlbumListAdapter {
        public String getFBToken();

        public void selected(Album album);
    }

}
