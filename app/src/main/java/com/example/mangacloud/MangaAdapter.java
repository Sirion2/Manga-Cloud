package com.example.mangacloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


// Display manga to Recycler View

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {

    Context context;
    ArrayList<Mangalist_item> MangaVolumensArrayList;
    private  OnItemListener mOnItemListener;

    public MangaAdapter (Context context, ArrayList<Mangalist_item> mangaVolumensArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.MangaVolumensArrayList = mangaVolumensArrayList;
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public MangaAdapter.MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.volume_list, parent, false);

        return new MangaViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaViewHolder holder, int position) {
        Mangalist_item mangalist_item = MangaVolumensArrayList.get(position);
            holder.VOLUMENES.setText(   mangalist_item.VOLUMENES);
    }

    @Override
    public  int getItemCount() {return MangaVolumensArrayList.size();}

    public static class MangaViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView VOLUMENES;
        OnItemListener onItemListener;

        public MangaViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            VOLUMENES = itemView.findViewById(R.id.manga_manga_vnumero);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public  void onClick(View view) {onItemListener.onItemClick(getAdapterPosition());}
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
