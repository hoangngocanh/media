package com.example.ngocanhpro.media.adapter;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.db.DBHandler;
import com.example.ngocanhpro.media.enity.Playlist;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.fragment.FragmentListSong;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;



public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Song> mSongs = new ArrayList<>();
    private ArrayList<Song> mResutlFillList = new ArrayList<>();
    private ImageLoader mImageLoader;
    public FragmentListSong fragmentListSong = new FragmentListSong();
    private Context mContext;
    private ArrayList<String> mArray = new ArrayList<>();
    IControlPlayMedia iControlPlayMedia;
    private Song mSong;
    //init data base
    private DBHandler mDB;
    private CustomFilter mFilter;



    public interface IOnItemClickListener {
        void onItemClick(Song item);
    }

    private IOnItemClickListener listener;

    public SongAdapter(ArrayList<Song> mSongs, IOnItemClickListener listener, Context context) {
        this.mSongs = mSongs;
        this.listener = listener;
        this.mContext = context;
        this.iControlPlayMedia = (IControlPlayMedia) context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mDB = new DBHandler(mContext);

        mFilter = new CustomFilter(this);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        mSong = mSongs.get(position);
        holder.title.setText(mSong.getTitle());
        holder.artist.setText(mSong.getArtist());
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, mSong.getAlbumID());
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
                Log.v(">>>>>>>>>>"," "+position);
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
//                              //Hiển thị cửa sổ add playlist
                                showListAddPlaylist(position);
                                return true;
                            case R.id.nav_delete:
                                // Xóa bài hát
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
        public void bind(final Song item, final IOnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    //Hiển thị cửa số chứa text edit để gõ tên playlist mới, tạo playlist mới
    public void showDialogAddPlaylist(int position) {
        mSong = mSongs.get(position);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.popup_new_playlist);
        final EditText editPlayList = (EditText) dialog.findViewById(R.id.edit_playlist);
        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editPlayList.getText().toString();
                if (name != "" || name != null) {
                    int idPlaylist = mDB.addPlayList(name); // tạo mới 1 playlist
                    long idAlbum, idSong;
                    String nameSong;
                    String artist;
                    idAlbum = mSong.getAlbumID();
                    idSong = mSong.getId();
                    nameSong = mSong.getTitle();
                    artist = mSong.getArtist();
                    mDB.addSongToPlayList(idPlaylist, idSong, nameSong, artist, idAlbum);

                }
                Toast.makeText(mContext.getApplicationContext(),"Tạo playlist "+name,Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    //Hiển thị cửa sổ chứa new (add playlist mới) và các playlist hiện có
    public void showListAddPlaylist(final int position) {
        mSong = mSongs.get(position);
        mArray.clear();
        mArray.add("New +");
        final ArrayList<Playlist> playlists = mDB.selectAllPlaylist();
        for(int i=0; i < playlists.size(); i++) {
            mArray.add(playlists.get(i).getName());
        }
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_list_playlist);
        ListView lv = (ListView) dialog.findViewById(R.id.lv_playlist);
        ArrayAdapter adapter = new ArrayAdapter<String>(mContext,R.layout.item_dialog_playlist, mArray);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                if (pos == 0) {
                    showDialogAddPlaylist(position);

                    dialog.dismiss();
                } else if (pos > 0) {
                    long idPlaylist, idAlbum, idSong;
                    String nameSong;
                    String artist;
                    idPlaylist = playlists.get(pos-1).getId();
                    idAlbum = mSong.getAlbumID();
                    idSong = mSong.getId();
                    nameSong = mSong.getTitle();
                    artist = mSong.getArtist();
                    mDB.addSongToPlayList(idPlaylist, idSong, nameSong, artist, idAlbum);
                    dialog.dismiss();
                }

            }

        });
        dialog.show();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    //bộ lọc trả về kết quả adapter song  theo thanh search
    public class CustomFilter extends Filter {
        private SongAdapter mSongAdapter;
        public CustomFilter(SongAdapter mAdapter) {
            super();
            this.mSongAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mResutlFillList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
//                mResutlFillList.addAll(mSongs);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final Song mWords : mSongs) {
                    if (mWords.getTitle().toLowerCase().startsWith(filterPattern)) {
                        mResutlFillList.add(mWords);
                    }
                }
            }
            System.out.println("Count Number " + mResutlFillList.size());
            results.values = mResutlFillList;
            results.count = mResutlFillList.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mSongAdapter.notifyDataSetChanged();
        }
    }
}

