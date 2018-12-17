package com.ibm.hamsafar.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.ListObjects;

import java.util.List;

public class SpinnerAdapter implements android.widget.SpinnerAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<ListObjects> items;

    public SpinnerAdapter(@NonNull Context context,
                          @NonNull List objects) {


        mContext = context;
        mInflater = LayoutInflater.from(context);
        items = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.spinner_row, parent, false);

        TextView offTypeTv = (TextView) view.findViewById(R.id.spinnerItem);

        ListObjects listItem = items.get(position);

        offTypeTv.setText(listItem.getTitle());
        return view;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ListObjects getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }


}

