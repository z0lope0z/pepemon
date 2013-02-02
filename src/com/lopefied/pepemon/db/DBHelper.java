package com.lopefied.pepemon.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;

/**
 * 
 * @author lemano
 * 
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "pepemon_database.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Album, Integer> albumTable = null;
    private Dao<Photo, Integer> photoTable = null;
    private ConnectionSource connectionSource = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            TableUtils.createTable(connectionSource, Album.class);
            TableUtils.createTable(connectionSource, Photo.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
            int oldVersion, int newVersion) {
    }

    public Dao<Album, Integer> getAlbumDao() throws SQLException {
        if (albumTable == null) {
            albumTable = getDao(Album.class);
        }
        return albumTable;
    }

    public Dao<Photo, Integer> getPhotoDao() throws SQLException {
        if (photoTable == null) {
            photoTable = getDao(Photo.class);
        }
        return photoTable;
    }
}