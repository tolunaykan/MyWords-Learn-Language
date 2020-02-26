package com.tolunaykandirmaz.mywords.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tolunaykandirmaz.mywords.Interfaces.ItemClickListener;
import com.tolunaykandirmaz.mywords.R;

import java.util.ArrayList;

public class ImageSearchListAdapter extends RecyclerView.Adapter<ImageSearchListAdapter.MyViewHolder> {

    private ArrayList<String> data;
    private ItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView3);
        }
    }

    public ImageSearchListAdapter(ArrayList<String> data, ItemClickListener listener){
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_search_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Picasso.get().load(data.get(position)).fit().into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(data.get(position));
            }
        });
    }



    @Override
    public int getItemCount() {
        return data.size();
    }
}
