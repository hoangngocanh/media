package com.example.ngocanhpro.media;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ngocanhpro.media.enity.Kind;

import java.util.ArrayList;

public class KindAdapter extends RecyclerView.Adapter<KindAdapter.MyViewHolder> {
    private ArrayList<Kind> mKinds = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public interface OnItemClickListener {
        void onItemClick(Kind item);
    }
    private OnItemClickListener listener;

    public KindAdapter(ArrayList<Kind> mKinds, OnItemClickListener listener) {
        this.mKinds = mKinds;
        this.listener = listener;
    }

    public KindAdapter(ArrayList<Kind> kinds) {
        this.mKinds = kinds;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kind_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Kind song = mKinds.get(position);
        holder.tvName.setText(song.getName());
        holder.tvNum.setText(song.getNum()+"");
        holder.bind(mKinds.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return mKinds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvNum;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvNum = (TextView) view.findViewById(R.id.tv_num);
        }
        public void bind(final Kind item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

