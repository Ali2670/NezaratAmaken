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
import android.widget.Button;
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

public class EditChecklistActivity extends Activity {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView recyclerView = null;
    private FloatingActionButton addItem = null;
    private LinearLayoutManager linearLayoutManager = null;
    private CheckItemAdapter adapter = null;
    private List<CheckItem> listData;
    private CoordinatorLayout coordinatorLayout = null;
    private Button save = null;
    private Button cancel = null;
    private TripInfo tripInfo = null;
    private static boolean has_trip = false;

    //trip card
    private LinearLayout tripParent = null;
    private TextView tripCardPort = null;
    private TextView tripCardDestination = null;
    private TextView tripCardStart = null;
    private TextView tripCardEnd = null;
    private TextView tripCardTrans = null;
    private Integer cl_id = null;


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
        save = findViewById(R.id.cl_done_btn);
        cancel = findViewById(R.id.cl_cancel_btn);

        tripParent = findViewById(R.id.trip_card_parent);
        tripCardPort = findViewById(R.id.trip_card_port);
        tripCardDestination = findViewById(R.id.trip_card_destination);
        tripCardStart = findViewById(R.id.trip_card_start_date);
        tripCardEnd = findViewById(R.id.trip_card_end_date);
        tripCardTrans = findViewById(R.id.trip_card_transport);


        //get checklist id
        if( getIntent().hasExtra("checklist_id") ) {
            cl_id = getIntent().getIntExtra("checklist_id", 0 );
        }

        //------------------------------------------------------------------------------------------
        /*
         * if list is assigned to a trip, getTrip info from db and show in trip parent
         * else set trip parent GONE
         */
        tripInfo = new TripInfo();

        /*else {
            has_trip = false;
            tripParent.setVisibility( View.GONE );
        }*/

        //------------------------------------------------------------------------------------------

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText( getResources().getString(R.string.cl_list_edit_title));

        linearLayoutManager = new LinearLayoutManager(EditChecklistActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getCheckList();
        adapter = new CheckItemAdapter(this, listData);
        recyclerView.setAdapter(adapter);


        addItem.setOnClickListener(view -> {
            Intent intent  = new Intent( EditChecklistActivity.this, CheckListItemEditActivity.class);
            startActivity( intent );
        });

        cancel.setOnClickListener(view -> finish());

        save.setOnClickListener(view -> {
            updateChecklist();
            finish();
        });

    }


    //update checklist info into DB
    private void updateChecklist() {

    }

    //----------------------------------------------------------------------------------------------
    //download checklist items
    private void getCheckList() {

        /*
        * get checklist info from db using its id
        * */

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
    //----------------------------------------------------------------------------------------------



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditChecklistActivity.this);
        builder.setMessage(getResources().getString(R.string.cl_exit_message));
        builder.setPositiveButton(getResources().getString(R.string.cl_save_changes),
                (dialogInterface, i) -> {
                    updateChecklist();
                    finish();
                });
        builder.setNegativeButton(getResources().getString(R.string.cl_cancel),
                (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
        builder.setNeutralButton(getResources().getString(R.string.cl_discard),
                (dialogInterface, i) -> {
                    //get checklist from DB again
                    dialogInterface.cancel();
                });
        builder.show();
    }
}
