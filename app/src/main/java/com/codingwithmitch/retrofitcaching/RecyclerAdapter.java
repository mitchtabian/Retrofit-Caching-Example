package com.codingwithmitch.retrofitcaching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PhotosViewHolder> {

    private RequestManager requestManager;
    private List<Photo> photos = new ArrayList<>();

    public RecyclerAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photo_list_item, parent, false);
        return new PhotosViewHolder(view, requestManager);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosViewHolder holder, int position) {
        holder.bind(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<Photo> photos){
        this.photos = photos;
        notifyDataSetChanged();
    }

    public class PhotosViewHolder extends RecyclerView.ViewHolder{

        private RequestManager requestManager;
        private ImageView image;
        private TextView title;

        public PhotosViewHolder(@NonNull View itemView, RequestManager requestManager) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            this.requestManager = requestManager;
        }

        public void bind(Photo photo){
            requestManager
                    .load(photo.getUrl())
                    .into(image);
            title.setText(photo.getTitle());
        }
    }

}













