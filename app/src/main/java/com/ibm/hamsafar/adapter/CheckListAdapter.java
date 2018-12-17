package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ibm.hamsafar.R;

/**
 * Created by Wizard on 12/11/2017.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private Context context;
    private String[] items;

    public CheckListAdapter(Context context, String[] items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_item,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.item.setText(items[position]);
    }



    @Override
    public int getItemCount() {

        return items.length;
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public CheckBox item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.check_list_item_cb);
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {

        }
    }

}