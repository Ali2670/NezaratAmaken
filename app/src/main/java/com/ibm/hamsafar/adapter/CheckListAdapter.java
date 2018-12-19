package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_item,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.item.setText(items[position]);

        holder.setItemLongClickListener((v, pos) -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listViewDialog = inflater.inflate(R.layout.alertdialog_with_list, null);
            builder.setView(listViewDialog);
            TextView listTitle = listViewDialog.findViewById(R.id.listAlertDialogTitle);
            listTitle.setText(items[position]);
            ListView listView = listViewDialog.findViewById(R.id.dialogList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_expandable_list_item,
                    context.getResources().getStringArray(R.array.checklist_item_menu));
            listView.setAdapter(adapter);
            final AlertDialog dialog = builder.create();
            dialog.show();
            listView.setOnItemClickListener((parent, view1, position1, id) -> {
                String selectedListItem = ((TextView) view1).getText().toString();
                dialog.dismiss();

                switch ( selectedListItem ) {
                    case "ویرایش" :

                        break;
                    case "حذف" :

                        break;
                    case "آلارم" :

                        break;
                }
            });
        });
    }



    @Override
    public int getItemCount() {

        return items.length;
    }


    public  class ViewHolder extends  RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView item;
        public CardView cardView;
        ItemLongClickListener itemLongClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.check_list_item_text);
            cardView = itemView.findViewById(R.id.check_list_item_card);

            itemView.setOnLongClickListener(this);
        }


        public void setItemLongClickListener(ItemLongClickListener ic) {
            this.itemLongClickListener=ic;
        }

        @Override
        public boolean onLongClick(View v) {
            this.itemLongClickListener.onItemLongClick(v,getLayoutPosition());
            return false;
        }
    }


}