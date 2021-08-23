package com.example.chatapp.Chat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;

import java.util.ArrayList;
//used to populate recycle view
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{

    ArrayList<String> mediaList;
    Context context;
    public MediaAdapter(Context context, ArrayList<String> mediaList){
        this.context=context;
        this.mediaList=mediaList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media,null,false);
        MediaViewHolder mediaViewHolder=new MediaViewHolder(layoutView);
        return mediaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        //used to load the uris to the imageview
        Glide.with(context).load(Uri.parse(mediaList.get(position))).into(holder.mMedia);

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }


    //Its the container of each individual element then the Recycler view combines it with the data
    public class MediaViewHolder extends RecyclerView.ViewHolder{

        ImageView mMedia;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mMedia=itemView.findViewById(R.id.media);
        }
    }
}
