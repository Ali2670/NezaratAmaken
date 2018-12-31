package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.ChecklistAdapter;
import com.ibm.hamsafar.object.Checklist;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/24/2018.
 */
public class ChecklistListActivity extends Activity {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView recyclerView = null;
    private FloatingActionButton add = null;
    private LinearLayoutManager linearLayoutManager = null;
    private ChecklistAdapter adapter;
    private List<Checklist> listData;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.checklist_list);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.cl_list_recycler_view);
        add = findViewById(R.id.cl_list_add);

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText(getResources().getString(R.string.cl_list_title));

        linearLayoutManager = new LinearLayoutManager(ChecklistListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getChecklistList();
        adapter = new ChecklistAdapter(context, listData);
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(view -> startActivity(new Intent(ChecklistListActivity.this, CheckListActivity.class)));

    }

    private void getChecklistList() {
        listData = new ArrayList<>();
        for (int i = 21; i > 0; i--) {
            Checklist checklist = new Checklist();
            checklist.setId(i);
            checklist.setItem_one("1st item " + i);
            checklist.setItem_two("2nd item " + i);
            checklist.setItem_three("3rd item " + i);
            listData.add(checklist);
        }
    }


}
