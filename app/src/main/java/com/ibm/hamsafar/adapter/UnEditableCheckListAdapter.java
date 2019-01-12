package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.CheckItem;

import java.util.List;

/**
 * Created by Wizard on 12/11/2017.
 */

public class UnEditableCheckListAdapter extends RecyclerView.Adapter<UnEditableCheckListAdapter.ViewHolder> {

    private Context context;
    private List<CheckItem> items;

    public UnEditableCheckListAdapter(Context context, List<CheckItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.topic.setText(items.get(position).getTopic());
        holder.date.setText(items.get(position).getDate());

        holder.time.setText(items.get(position).getTime());

        if (items.get(position).isChecked()) {
            holder.checkBox.setChecked(true);
            holder.topic.setPaintFlags(holder.topic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.topic.setTextColor(context.getResources().getColor(R.color.checked_item));
        } else {
            holder.checkBox.setChecked(false);
            holder.topic.setPaintFlags(holder.topic.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.topic.setTextColor(context.getResources().getColor(R.color.dark_text));

        }

        //on click for checkbox
        holder.checkBox.setOnClickListener(view -> {
            items.get(position).setChecked( holder.checkBox.isChecked() );
            if( holder.checkBox.isChecked() ) {
                holder.topic.setPaintFlags(holder.topic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.topic.setTextColor(context.getResources().getColor(R.color.checked_item));
            }
            else {
                holder.topic.setPaintFlags(holder.topic.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.topic.setTextColor(context.getResources().getColor(R.color.dark_text));
            }
        });

        if (items.get(position).getTime().equals("")) {
            holder.time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_dark, 0, 0, 0);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView topic;
        private TextView date;
        private TextView time;
        public CardView cardView;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.check_list_item_text);
            date = itemView.findViewById(R.id.check_list_item_alarm_date);
            time = itemView.findViewById(R.id.check_list_item_alarm_time);
            cardView = itemView.findViewById(R.id.check_list_item_card);
            checkBox = itemView.findViewById(R.id.check_list_item_check);
        }
    }

}