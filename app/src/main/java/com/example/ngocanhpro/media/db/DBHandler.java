package com.example.ngocanhpro.media.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ngocanhpro.media.enity.Playlist;
import com.example.ngocanhpro.media.enity.Song;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PlayListInfo";

    // Tên các bảng
    private static final String PLAYLISTS = "list_playlist"; // Bảng danh sách các playlist
    private static final String LISTSONG = "list_song";        // Bảng danh sách các bài hát của mỗi playlist

    // các cột của bảng list_playlist
    private static final String KEY_ID = "id";
    private static final String NAME_PLAYLIST = "name";
    private static final String NUMBER = "number";
    // các cột của bảng list_song
    private static final String ARTIST = "artist";
    private static final String IDALBUM = "id_album";
    private static final String NAMESONG = "name_song";
    private static final String IDSONG = "id_song";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_LIST_PLAYLIST = "CREATE TABLE " + PLAYLISTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY autoincrement ," + NAME_PLAYLIST + " TEXT," + NUMBER + " INTEGER" +")";
        db.execSQL(CREATE_TABLE_LIST_PLAYLIST);

        String CREATE_TABLE_LIST_SONG = "CREATE TABLE " + LISTSONG + "(" + KEY_ID + " INTEGER,"
                + IDSONG + " INTEGER, " + NAMESONG + " TEXT, " + ARTIST + " TEXT," + IDALBUM + " INTEGER,"
                + " FOREIGN KEY ("+KEY_ID+") REFERENCES "+PLAYLISTS+"("+KEY_ID+")"
                + " ON DELETE CASCADE );"; // on delete cascade để có thể xóa dữ liệu từ bảng phụ có khóa ngoại
        db.execSQL(CREATE_TABLE_LIST_SONG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + LISTSONG);

        onCreate(db);
    }

    //Thêm 1 playlist vào bảng
    public int addPlayList(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME_PLAYLIST, name); // Playlist Name
        values.put(NUMBER, 0);

        // Inserting Row
        int id;
        id = (int) db.insert(PLAYLISTS, null, values);
        db.close(); // Closing database connection
        return id;
    }

    //Lấy dữ liệu toàn bộ danh sách playlist
    public ArrayList<Playlist> selectAllPlaylist() {
        ArrayList<Playlist> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ PLAYLISTS;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                arrayList.add(new Playlist(Integer.parseInt(c.getString(0)), c.getString(1)));
            }
            while (c.moveToNext());
        }
        return arrayList;
    }


    //Xóa 1 hàng playlist theo id
    public boolean deletePlayList(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(PLAYLISTS, KEY_ID + "= "+id, null) > 0;
    }

    // Thêm bài hát vào  playlist
    public void addSongToPlayList (long id,long idSong, String name, String artist, long idAlbum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(IDSONG, idSong);
        values.put(NAMESONG, name);
        values.put(ARTIST, artist);
        values.put(IDALBUM, idAlbum);

        // Inserting Row
        db.insert(LISTSONG, null, values);
        db.close(); // Closing database connection
    }

    //Lấy dữ liệu toàn bộ danh sách baì hát theo playlist
    public ArrayList<Song> selectListSong() {
        ArrayList<Song> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ LISTSONG;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                long id = Long.parseLong(c.getString(1));
                String nameSong = c.getString(2);
                String artist = c.getString(3);
                long idAlbum = Long.parseLong(c.getString(4));
                arrayList.add(new Song(id, nameSong, artist, idAlbum));
            }
            while (c.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<Song> selectListSongByIdPlaylist(long idPlaylist) {
        ArrayList<Song> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ LISTSONG+" WHERE id= "+idPlaylist;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                long id = Long.parseLong(c.getString(1));
                String nameSong = c.getString(2);
                String artist = c.getString(3);
                long idAlbum = Long.parseLong(c.getString(4));
                arrayList.add(new Song(id, nameSong, artist, idAlbum));
            }
            while (c.moveToNext());
        }
        return arrayList;
    }

    //Xóa 1 bài hát theo id ra khỏi playlist
    public boolean deleteSong(int id, long idSong)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(LISTSONG, IDSONG + "= "+idSong +"AND "+KEY_ID+" = "+id, null) > 0;
    }

    //Xóa tất cả bài hát của 1 playlist
    public boolean deleteSongPlaylist(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(LISTSONG, KEY_ID+" = "+id, null) > 0;
    }

}
