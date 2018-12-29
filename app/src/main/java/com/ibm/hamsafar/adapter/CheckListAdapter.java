package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.activity.EditTripActivity;
import com.ibm.hamsafar.object.TripInfo;
import com.ibm.hamsafar.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by maryam on 12/24/2018.
 */
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private Context context;
    private List<TripInfo> items;
    private static Integer current_position = null;

    public ChecklistAdapter(Context context, List<TripInfo> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if( expired(position)) {
            holder.title.setVisibility( View.VISIBLE );
            holder.title.setBackgroundColor( context.getResources().getColor(R.color.expired_trip));
            holder.title.setText(context.getResources().getString(R.string.trip_list_expired));
        }
        else {
            holder.title.setVisibility( View.GONE );
        }
        holder.port.setText( items.get(position).getPort() );
        holder.destination.setText( items.get(position).getDes() );
        holder.start.setText( items.get(position).getStart() );
        holder.end.setText( items.get(position).getEnd() );
        holder.trans.setText( items.get(position).getTrans() );

        //long click for whole item
        holder.setItemLongClickListener((v, pos) -> {
            current_position = position;
            openLongClickMenu(position);
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView title;
        private TextView port;
        private TextView destination;
        private TextView start;
        private TextView end;
        private TextView trans;
        ItemLongClickListener itemLongClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.trip_card_title);
            port = itemView.findViewById(R.id.trip_card_port);
            destination = itemView.findViewById(R.id.trip_card_destination);
            start = itemView.findViewById(R.id.trip_card_start_date);
            end = itemView.findViewById(R.id.trip_card_end_date);
            trans = itemView.findViewById(R.id.trip_card_transport);

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
        listTitle.setText(items.get(position).getDes());
        ListView listView = listViewDialog.findViewById(R.id.dialogList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_expandable_list_item,
                context.getResources().getStringArray(R.array.trip_item_menu));
        listView.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener((parent, view1, position1, id) -> {
            String selectedListItem = ((TextView) view1).getText().toString();

            if (selectedListItem.equals("ویرایش")) {
                Intent intent = new Intent(context, EditTripActivity.class);
                intent.putExtra("trip", items.get(position));
                context.startActivity(intent);
            }
            else if (selectedListItem.equals("حذف")) {
                delete( position );
            }
            dialog.dismiss();
        });
    }

    //check trip expiration
    private  boolean expired( int position ) {
        Date  end = DateUtil.toDate( items.get(position).getEnd() );
        Date now = DateUtil.toDate(DateUtil.getCurrentDate());
        if( now.after(end) ) return true;
        else return false;
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
