package com.example.moviefx;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    int imgAction[];
    private OnMovieListener monMovieListener;

    public Adapter(Context context, int imgAction[], OnMovieListener monMovieListener) {
        this.context = context;
        this.imgAction = imgAction;
        this.monMovieListener = monMovieListener;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, monMovieListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.textView.setText(data[position]);
        holder.imageView.setImageResource(imgAction[position]);

    }

    @Override
    public int getItemCount() {
        return imgAction.length;
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
            onMovieListener.onMovieClick(getAdapterPosition());
        }
    }
    public interface OnMovieListener{
        void onMovieClick(int position);

    }
}