package com.example.ngocanhpro.media.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.enity.Album;
import com.example.ngocanhpro.media.enity.Playlist;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
    private ArrayList<Playlist> mPlaylists = new ArrayList<>();
    private ImageLoader mImageLoader;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(Playlist item);
    }



    private OnItemClickListener listener;

    public PlaylistAdapter(ArrayList<Playlist> arrayList, OnItemClickListener listener, Context context) {
        this.mPlaylists = arrayList;
        this.listener = listener;
        this.mContext = context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Playlist playlist = mPlaylists.get(position);
        holder.tvName.setText(playlist.getName());
        holder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.btnExpand);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_expand_playlist);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_play:
                                return true;
                            case R.id.nav_rename:
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
        holder.bind(mPlaylists.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView img;
        public Button btnExpand;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_namePlaylist);
            btnExpand = (Button) view.findViewById(R.id.btn_expand);

        }
        public void bind(final Playlist item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

