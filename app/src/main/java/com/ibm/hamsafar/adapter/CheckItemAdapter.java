package com.ibm.hamsafar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hamsafar.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.hamsafar.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.activity.CheckListItemEditActivity;
import com.ibm.hamsafar.object.CheckItem;

import java.util.List;

/**
 * Created by Wizard on 12/11/2017.
 */

public class CheckItemAdapter extends RecyclerView.Adapter<CheckItemAdapter.ViewHolder> implements TimePickerDialog.OnTimeSetListener {

    private Context context;
    private List<CheckItem> items;
    private static Integer current_position = null;

    public CheckItemAdapter(Context context, List<CheckItem> items) {
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

        if( items.get(position).isChecked() ) {
            holder.checkBox.setChecked( true );
            holder.topic.setPaintFlags(holder.topic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.topic.setTextColor(context.getResources().getColor(R.color.checked_item));
        } else {
            holder.checkBox.setChecked( false );
            holder.topic.setPaintFlags(holder.topic.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.topic.setTextColor(context.getResources().getColor(R.color.dark_text));

        }

        //long click for whole item
        holder.setItemLongClickListener((v, pos) -> {
            current_position = position;
            openLongClickMenu(position);
        });

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
        }
        if (!items.get(position).getTime().equals("")) {
            holder.time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_dark, 0, 0, 0);
        }

        if (items.get(position).getDate().equals("")) {
            holder.date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        if (!items.get(position).getDate().equals("")) {
            holder.date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_date_dark, 0);
        }
    }


    @Override
    public int getItemCount() {

        return items.size();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = hourString + ":" + minuteString;
        items.get(current_position).setTime(time);
        //notifyDataSetChanged();
        notifyItemChanged(current_position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView topic;
        private TextView date;
        private TextView time;
        public CardView cardView;
        public CheckBox checkBox;
        ItemLongClickListener itemLongClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.check_list_item_text);
            date = itemView.findViewById(R.id.check_list_item_alarm_date);
            time = itemView.findViewById(R.id.check_list_item_alarm_time);
            cardView = itemView.findViewById(R.id.check_list_item_card);
            checkBox = itemView.findViewById(R.id.check_list_item_check);

            itemView.setOnLongClickListener(this);
        }


        public void setItemLongClickListener(ItemLongClickListener ic) {
            this.itemLongClickListener = ic;
        }

        @Override
        public boolean onLongClick(View v) {
            this.itemLongClickListener.onItemLongClick(v, getLayoutPosition());
            return false;
        }
    }


    //open menu used for item long click
    private void openLongClickMenu(int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewDialog = inflater.inflate(R.layout.alertdialog_with_list, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.listAlertDialogTitle);
        listTitle.setText(items.get(position).getTopic());
        ListView listView = listViewDialog.findViewById(R.id.dialogList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_expandable_list_item,
                context.getResources().getStringArray(R.array.check_item_menu));
        listView.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener((parent, view1, position1, id) -> {
            String selectedListItem = ((TextView) view1).getText().toString();

            if (selectedListItem.equals("ویرایش")) {
                Intent intent = new Intent(context, CheckListItemEditActivity.class);
                intent.putExtra("check_item", items.get(position));
                intent.putExtra("from", "list");
                context.startActivity(intent);
            }
            else if (selectedListItem.equals("حذف")) {
                delete( position );
            }
            else if (selectedListItem.equals("آلارم")) {
                PersianCalendar now = new PersianCalendar();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        this,
                        now.get(PersianCalendar.HOUR_OF_DAY),
                        now.get(PersianCalendar.MINUTE),
                        true
                );
                timePickerDialog.show(((Activity) context).getFragmentManager(), "TimePickerDialog");
            }
            dialog.dismiss();
        });
    }


    //open delete menu and its action
    private void delete(int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View deleteDialog = inflater.inflate(R.layout.dialog_tow_button, null);
        builder.setView(deleteDialog);

        Button close = deleteDialog.findViewById(R.id.dialog_toolbar_close);
        TextView title = deleteDialog.findViewById(R.id.dialog_toolbar_title);
        TextView message = deleteDialog.findViewById(R.id.dialog_two_message);
        Button positive = deleteDialog.findViewById(R.id.dialog_two_positive_btn);
        Button negative = deleteDialog.findViewById(R.id.dialog_two_negative_btn);

        title.setText( context.getResources().getString(R.string.delete_title));
        message.setText( context.getResources().getString(R.string.delete_message));
        positive.setText( context.getResources().getString(R.string.delete_positive));
        negative.setText( context.getResources().getString(R.string.delete_negative));

        final AlertDialog dialog = builder.create();
        dialog.show();

        close.setOnClickListener(view -> dialog.dismiss());

        negative.setOnClickListener(view -> dialog.dismiss());

        positive.setOnClickListener(view -> {
            /*
            * delete item from db too
            * */
            items.remove( position );
            notifyDataSetChanged();
            dialog.dismiss();
        });
    }

}