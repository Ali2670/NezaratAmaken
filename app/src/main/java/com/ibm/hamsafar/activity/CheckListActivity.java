package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.CheckItemAdapter;
import com.ibm.hamsafar.object.CheckItem;
import com.ibm.hamsafar.object.TripInfo;
import com.ibm.hamsafar.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckListActivity extends Activity {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView recyclerView = null;
    private FloatingActionButton addItem = null;
    private LinearLayoutManager linearLayoutManager = null;
    private CheckItemAdapter adapter = null;
    private List<CheckItem> listData;
    private CoordinatorLayout coordinatorLayout = null;
    private Button done = null;
    private Button cancel = null;
    private EditText title = null;
    private TripInfo tripInfo = null;
    private static boolean has_trip = false;

    //trip card
    private LinearLayout tripParent = null;
    private TextView tripCardPort = null;
    private TextView tripCardDestination = null;
    private TextView tripCardStart = null;
    private TextView tripCardEnd = null;
    private TextView tripCardTrans = null;

    private static Integer trip_id = 0;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.cl_recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        addItem = findViewById(R.id.cl_add_item);
        done = findViewById(R.id.cl_done_btn);
        cancel = findViewById(R.id.cl_cancel_btn);
        title = findViewById(R.id.cl_title );


        tripParent = findViewById(R.id.trip_card_parent);
        tripCardPort = findViewById(R.id.trip_card_port);
        tripCardDestination = findViewById(R.id.trip_card_destination);
        tripCardStart = findViewById(R.id.trip_card_start_date);
        tripCardEnd = findViewById(R.id.trip_card_end_date);
        tripCardTrans = findViewById(R.id.trip_card_transport);

        /*
        * set proper date for check items
        * trip date if related to a trip
        * current date if no trip is assigned
        * */
        tripInfo = new TripInfo();
        if( getIntent().hasExtra("trip_info") ) {
            tripInfo = (TripInfo) getIntent().getSerializableExtra("trip_info");
            has_trip = true;
            tripCardPort.setText( tripInfo.getPort() );
            tripCardDestination.setText( tripInfo.getDes() );
            tripCardStart.setText( tripInfo.getStart() );
            tripCardEnd.setText( tripInfo.getEnd() );
            tripCardTrans.setText( tripInfo.getTrans() );
            trip_id = tripInfo.getId();
        }
        else {
            has_trip = false;
            tripParent.setVisibility( View.GONE );
        }

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText( getResources().getString(R.string.check_list_title));

        linearLayoutManager = new LinearLayoutManager(CheckListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getCheckList();
        adapter = new CheckItemAdapter(this, listData);
        recyclerView.setAdapter(adapter);


        addItem.setOnClickListener(view -> {
            Intent intent  = new Intent( CheckListActivity.this, CheckListItemEditActivity.class);
            startActivity( intent );
        });

        cancel.setOnClickListener(view -> {
            finish();
        });

        done.setOnClickListener(view -> {
            saveChecklistIntoDB();
            finish();
        });

    }

    //save checklist info into DB
    private void saveChecklistIntoDB() {
        if( trip_id == 0 ) {
            //save without trip id
        }
        else {
            //save using trip id
        }
    }

    //download checklist items
    private void getCheckList() {
        listData = new ArrayList<>();
        for(int i=0; i<21; i++ ) {
            CheckItem checkItem = new CheckItem();
            checkItem.setId(i);
            checkItem.setTopic("topic" + i);

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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder( CheckListActivity.this );
        builder.setMessage()
    }
}
