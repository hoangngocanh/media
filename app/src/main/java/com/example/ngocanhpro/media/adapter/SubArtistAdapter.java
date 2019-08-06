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

/**
 * Created by Aroliant on 1/3/2018.
 */

public class SubArtistAdapter extends RecyclerView.Adapter<SubArtistAdapter.ViewHolder> {
    private ArrayList<Album> mAlbumList = new ArrayList<>();
    private ImageLoader mImageLoader;
    private Context mContext;

    public interface IOnItemClickListener {
        void onItemClick(Album item);
    }
    private IOnItemClickListener listener;


    public SubArtistAdapter(ArrayList<Album> list, Context context, IOnItemClickListener listener) {
        this.mContext = context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        this.mAlbumList =list;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfor;
        ImageView imgAlbum;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.album_name);
            tvInfor = itemView.findViewById(R.id.album_infor);
            imgAlbum = itemView.findViewById(R.id.img_sub_album);
        }

        public void bind(final Album item, final IOnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_dropview, parent, false);


        SubArtistAdapter.ViewHolder vh = new SubArtistAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.tvName.setText(mAlbumList.get(position).getNameAlbum());
        holder.tvInfor.setText("Bài hát: "+mAlbumList.get(position).getNumSong());

        // Set hình ảnh cho từng albums
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, mAlbumList.get(position).getId());
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.loadImage(String.valueOf(albumArtUri), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.imgAlbum.setImageBitmap(loadedImage);
            }
        });

        holder.bind(mAlbumList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }



}
