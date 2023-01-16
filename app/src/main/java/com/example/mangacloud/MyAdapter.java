package com.example.mangacloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//Get all mangas to the main activity recycler view

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Mangalist_item> MangalistArrayList;
    private  OnItemListener mOnItemlistener;

    public MyAdapter(Context context, ArrayList<Mangalist_item> mangalistArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.MangalistArrayList = mangalistArrayList;
        this.mOnItemlistener = onItemListener;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.mangalist_item,parent,false);

        return new MyViewHolder(view, mOnItemlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mangalist_item mangalist_item = MangalistArrayList.get(position);
        holder.NOMBRE.setText(mangalist_item.NOMBRE);
        holder.DESCRIPCION.setText(mangalist_item.DESCRIPCION);
        Glide.with(context).load(mangalist_item.COVER).circleCrop().into(holder.COVER);
    }

    @Override
    public int getItemCount() {
        return MangalistArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView NOMBRE, DESCRIPCION, VOLUMENES;
        ImageView COVER;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            NOMBRE = itemView.findViewById(R.id.manga_titulo);
            DESCRIPCION = itemView.findViewById(R.id.manga_descripcion);
            COVER = itemView.findViewById(R.id.manga_imagen);
            //VOLUMENES = itemView.findViewById(R.id.output);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {onItemListener.onItemClick(getAdapterPosition());}
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
