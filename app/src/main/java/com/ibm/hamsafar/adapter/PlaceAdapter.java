package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.Place;

import java.util.List;

/**
 * Created by Wizard on 12/11/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Context context;
    private List<Place> places;

    public PlaceAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_card,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.name.setText( places.get(position).getName() );
        holder.phone.setText( places.get(position).getPhone() );
        holder.address.setText( places.get(position).getAddress() );

    }



    @Override
    public int getItemCount() {
        return places.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name;
        public TextView phone;
        public TextView address;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_card_name);
            phone = itemView.findViewById(R.id.place_card_phone);
            address = itemView.findViewById(R.id.place_card_address);
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {

        }
    }

}