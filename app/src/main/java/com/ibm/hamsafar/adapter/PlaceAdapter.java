package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ibm.hamsafar.R;

/**
 * Created by Wizard on 12/11/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Context context;
    private int[] posters;

    public PlaceAdapter(Context context, int[] posters) {
        this.context = context;
        this.posters = posters;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.image.setImageResource(posters[position]);
    }



    @Override
    public int getItemCount() {

        return posters.length;
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {

        }
    }

}