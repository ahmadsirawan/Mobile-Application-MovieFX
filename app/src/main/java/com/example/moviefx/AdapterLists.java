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

public class AdapterLists extends RecyclerView.Adapter<AdapterLists.ViewHolder> {

    Context context;
    int imgAction[];

    public AdapterLists(Context context, int imgAction[]) {
        this.context = context;
        this.imgAction = imgAction;
    }

    @NonNull
    @Override
    public AdapterLists.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_lists, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLists.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.textView.setText(data[position]);
        holder.imageView.setImageResource(imgAction[position]);
//        Glide.with(holder.itemView.getContext()).load(data.get(position)).into(holder.imageView);
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Clicked on " + data[position], Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return imgAction.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.textNames);
        }
    }
}