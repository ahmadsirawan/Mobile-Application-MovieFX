package com.example.moviefx;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterComedy extends RecyclerView.Adapter<AdapterComedy.ViewHolder> {

    Context context;
    private List<MovieModel> mData;
    private OnMovieListener monMovieListener;

    public AdapterComedy(Context context, List<MovieModel> mData, OnMovieListener monMovieListener) {
        this.context = context;
        this.mData = mData;
        this.monMovieListener = monMovieListener;
    }

    @NonNull
    @Override
    public AdapterComedy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, monMovieListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComedy.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.textView.setText(data[position]);
       // holder.imageView.setImageResource(imgAction[position]);
        // Using Glide library to dispaly the image
        Glide.with(context)
                .load(mData.get(position).getImg())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        ImageView imageView;
        OnMovieListener onMovieListener;


        public ViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.textNames);
            this.onMovieListener = onMovieListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMovieListener.onMovieClick3(getAdapterPosition());
        }
    }
    public interface OnMovieListener{
        void onMovieClick3(int position);

    }
}