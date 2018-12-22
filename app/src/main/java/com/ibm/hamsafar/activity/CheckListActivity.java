package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.CheckListAdapter;
import com.ibm.hamsafar.object.CheckItem;
import com.ibm.hamsafar.object.TripInfo;
import com.ibm.hamsafar.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class CheckListActivity extends Activity {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView recyclerView = null;
    private LinearLayoutManager linearLayoutManager = null;
    private CheckListAdapter adapter = null;
    private List<CheckItem> listData;
    private CoordinatorLayout coordinatorLayout = null;
    private TripInfo tripInfo = null;
    private static boolean has_trip = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_list);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.cl_recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        /*
        * set proper date for check items
        * trip date if related to a trip
        * current date if no trip is assigned
        * */
        tripInfo = new TripInfo();
        if( getIntent().hasExtra("trip_info") ) {
            tripInfo = (TripInfo) getIntent().getSerializableExtra("trip_info");
            has_trip = true;
        }
        else {
            has_trip = false;
        }

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText( getResources().getString(R.string.check_list_title));

        linearLayoutManager = new LinearLayoutManager(CheckListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getCheckList();
        adapter = new CheckListAdapter(this, listData);
        recyclerView.setAdapter(adapter);

    }

    //download checklist items
    private void getCheckList() {
        listData = new ArrayList<>();
        for(int i=0; i<21; i++ ) {
            CheckItem checkItem = new CheckItem();
            checkItem.setId(i);
            checkItem.setTopic("topic" + i);

            //checkItem.setDate("1397/05/" + i);
            if( has_trip ) {
                checkItem.setDate( tripInfo.getStart() );
            }
            else {
                checkItem.setDate(DateUtil.getCurrentDate() );
            }

            if( i % 3 == 0 )
                checkItem.setTime("10:" + i );
            else
                checkItem.setTime("");

            if ( i % 4 == 0 )
                checkItem.setChecked(true);
            else
                checkItem.setChecked( false );

            listData.add( checkItem );
        }
    }
}
