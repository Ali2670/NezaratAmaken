package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.Region;

import java.util.List;

/**
 * Created by maryam on 1/2/2019.
 */
public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

    private Context context;
    private List<Region> data;

    public RegionAdapter(Context context, List<Region> data ) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.region_card,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText( data.get(position).getTitle() );
        holder.id.setText( data.get(position).getId() );
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private TextView id;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.region_card_text);
            id = itemView.findViewById(R.id.region_card_id);
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {

        }
    }

}
