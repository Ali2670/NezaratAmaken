package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.CheckListAdapter;

public class NewCheckListActivity extends Activity {

    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView recyclerView = null;
    private LinearLayoutManager linearLayoutManager = null;
    private CheckListAdapter adapter = null;
    private String[] data;
    private CoordinatorLayout coordinatorLayout = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_check_list);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.cl_recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText( getResources().getString(R.string.check_list_title));

        linearLayoutManager = new LinearLayoutManager(NewCheckListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        data = getResources().getStringArray(R.array.checklist_items);
        adapter = new CheckListAdapter(this, data);
        recyclerView.setAdapter(adapter);

    }


    //item edit dialog
    public void openEditDialog
}
