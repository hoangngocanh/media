package com.example.ngocanhpro.media.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.db.DBHandler;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.fragment.FragmentListSong;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.os.Build.ID;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    private ArrayList<Song> mSongs = new ArrayList<>();
    private ImageLoader mImageLoader;
    public FragmentListSong fragmentListSong = new FragmentListSong();
    private Context mContext;
    IControlPlayMedia iControlPlayMedia;
    //init data base
    private DBHandler mDB;



    public interface OnItemClickListener {
        void onItemClick(Song item);
    }

    private OnItemClickListener listener;

    public SongAdapter(ArrayList<Song> mSongs, OnItemClickListener listener, Context context) {
        this.mSongs = mSongs;
        this.listener = listener;
        this.mContext = context;
        this.iControlPlayMedia = (IControlPlayMedia) context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mDB = new DBHandler(mContext);
    }

    public SongAdapter(ArrayList<Song> songs) {
        this.mSongs = songs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Song song = mSongs.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, song.getAlbumID());
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.loadImage(String.valueOf(albumArtUri),new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.imgSong.setImageBitmap(loadedImage);
            }
        });
        holder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.btnExpand);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_btn_expand);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_play:
                                iControlPlayMedia.setListSong(mSongs);
                                iControlPlayMedia.playSong(position);
                                return true;
                            case R.id.nav_add:
                                PopupMenu popup = new PopupMenu(mContext, holder.btnExpand);
                                //inflating menu from xml resource
                                popup.inflate(R.menu.menu_add_expand);
                                //adding click listener
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.nav_current_playlist:
                                                //handle menu2 click
                                                return true;
                                            case R.id.nav_new:
                                                //Tạo 1 playlist mới
                                                showPopup(view);
                                                return true;
                                            case R.id.nav_new1:
                                                //handle menu3 click
                                                return true;
                                            default:
                                                return false;
                                        }
                                    }
                                });
                                //displaying the popup
                                popup.show();
                                return true;
                            case R.id.nav_delete:
                                //handle menu3 click
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
        holder.bind(mSongs.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, artist;
        public ImageView imgSong;
        public Button btnExpand;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.song_artist);
            imgSong = (ImageView) view.findViewById(R.id.img_song);
            btnExpand = (Button) view.findViewById(R.id.btn_expand);
        }
        public void bind(final Song item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public void showPopup(View v) {

        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_new_playlist, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button btnSave = (Button) popupView.findViewById(R.id.btn_save);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        final EditText editPlayList = (EditText) popupView.findViewById(R.id.edit_playlist);

        popupWindow.setFocusable(true);
        popupWindow.update();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editPlayList.getText().toString();
                mDB.addPlayList(name); // tạo mới 1 playlist
                Toast.makeText(mContext.getApplicationContext(),"Tạo playlist "+name,Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();

            }
        });
        Button btnCancel = (Button) popupView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));


    }
}

