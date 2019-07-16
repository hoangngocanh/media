package com.example.ngocanhpro.media.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.enity.Album;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    private ArrayList<Album> mAlbum = new ArrayList<>();
    private ImageLoader mImageLoader;

    public interface OnItemClickListener {
        void onItemClick(Album item);
    }

    private OnItemClickListener listener;

    public AlbumAdapter(ArrayList<Album> arrayList, OnItemClickListener listener, Context context) {
        this.mAlbum = arrayList;
        this.listener = listener;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = mAlbum.get(position);
        holder.tvName.setText(album.getNameAlbum());
        holder.tvNum.setText("  Nghệ sĩ: "+ album.getNameArtist()+" - Bài hát: " + album.getNumSong());
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, album.getId());
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.loadImage(String.valueOf(albumArtUri),new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.imgArist.setImageBitmap(loadedImage);
            }
        });
        holder.bind(mAlbum.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return mAlbum.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvNum;
        public ImageView imgArist;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_nameAlbum);
            tvNum = (TextView) view.findViewById(R.id.tv_sub);
            imgArist = (ImageView) view.findViewById(R.id.img_album);
        }
        public void bind(final Album item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

