package com.example.ngocanhpro.media.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.enity.Album;
import com.example.ngocanhpro.media.enity.Artist;
import com.example.ngocanhpro.media.enity.Song;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder> {
    private ArrayList<Artist> mArtists = new ArrayList<>();
    private ImageLoader mImageLoader;
    ArrayList<Integer> counter = new ArrayList<Integer>(); // Kiểm soát onclick mở đóng tab artist
    Context context;
    private ArrayList<ArrayList> mArrayList = new ArrayList<>();



    public ArtistAdapter(ArrayList<Artist> arrayList, Context context) {
        this.mArtists = arrayList;
        this.context = context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));

        for (int i = 0; i < mArtists.size(); i++) {
            counter.add(0);
        }
    }

    public ArtistAdapter(ArrayList<Artist> arrayList, ArrayList<ArrayList> arrayAlbum, Context context) {
        this.mArrayList = arrayAlbum;
        this.mArtists = arrayList;
        this.context = context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));

        for (int i = 0; i < mArtists.size(); i++) {
            counter.add(0);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Artist artist = mArtists.get(position);
        holder.tvName.setText(artist.getName());
        holder.tvNum.setText("Bài hát: " + artist.getNumTrack()+"  Album: "+ artist.getNumAlbum());
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        if (artist.getListAlbum().size() > 0) {
            Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, artist.getListAlbum().get(0).getId());
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.loadImage(String.valueOf(albumArtUri), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    holder.imgArist.setImageBitmap(loadedImage);
                }
            });
        }


        SubArtistAdapter itemInnerRecyclerView = new SubArtistAdapter(mArtists.get(position).getListAlbum(), context,
            new SubArtistAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Album item) {

                }
        });


        holder.cardRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cardRecyclerView.setVisibility(View.VISIBLE);
                if (counter.get(position) % 2 == 0) {

                } else {
                    holder.cardRecyclerView.setVisibility(View.GONE);
                }

                counter.set(position, counter.get(position) + 1);
            }
        });
        holder.cardRecyclerView.setAdapter(itemInnerRecyclerView);

    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvNum;
        public ImageView imgArist;
        RecyclerView cardRecyclerView;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.artist_name);
            tvNum = (TextView) view.findViewById(R.id.artist_num);
            imgArist = (ImageView) view.findViewById(R.id.arist_img);
            cardRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
            cardView = itemView.findViewById(R.id.cardView);
        }

    }
}

