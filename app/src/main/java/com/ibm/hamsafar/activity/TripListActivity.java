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
import com.ibm.hamsafar.adapter.TripListAdapter;
import com.ibm.hamsafar.object.TripInfo;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/24/2018.
 */
public class TripListActivity extends Activity {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView recyclerView = null;
    private FloatingActionButton add = null;
    private LinearLayoutManager linearLayoutManager = null;
    private TripListAdapter adapter;
    private List<TripInfo> listData;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap( base ));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate( bundle );
        setContentView( R.layout.trip_list );

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.trip_list_recycler_view);
        add = findViewById(R.id.trip_list_add);

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText( getResources().getString(R.string.trip_list_title));

        linearLayoutManager = new LinearLayoutManager(TripListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getTripList();
        adapter = new TripListAdapter(this, listData);
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(view -> startActivity( new Intent( TripListActivity.this, TripActivity.class )));

    }

    private void getTripList() {
        listData = new ArrayList<>();
        for(int i=21; i>0; i-- ) {
            TripInfo tripInfo = new TripInfo();
            tripInfo.setPort( "port" + i );
            tripInfo.setDes( "destination" + i );
            tripInfo.setStart( "1397/09/" + i );
            tripInfo.setEnd( "1397/10/" + i );
            tripInfo.setTrans( "trans" + i );
            listData.add( tripInfo );
        }
    }


}
