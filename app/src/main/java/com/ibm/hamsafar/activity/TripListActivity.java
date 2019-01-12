package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.TripListAdapter;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.TripInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.TripDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.GetUserTripsRequest;
import hamsafar.ws.response.GetUserTripsResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
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
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.trip_list);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.trip_list_recycler_view);
        add = findViewById(R.id.trip_list_add);

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText(getResources().getString(R.string.trip_list_title));

        /*linearLayoutManager = new LinearLayoutManager(TripListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getTripList();
        adapter = new TripListAdapter(this, listData);
        recyclerView.setAdapter(adapter);*/

        getTripList();

        add.setOnClickListener(view -> startActivity(new Intent(TripListActivity.this, TripActivity.class)));

    }

    private void getTripList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        GetUserTripsRequest request = new GetUserTripsRequest();
        request.setUserId(user_id);

        TaskCallBack<Object> getUserTripsResponse = result -> {
            GetUserTripsResponse ress = JsonCodec.toObject((Map) result, GetUserTripsResponse.class);
            if (ress.getTripDtoList().size() != 0) {
                List<TripDto> trip_list;
                trip_list = ress.getTripDtoList();
                setTripList( trip_list );
                linearLayoutManager = new LinearLayoutManager(TripListActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new TripListAdapter(this, listData);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(context, "no trip", Toast.LENGTH_SHORT).show();
            }
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(getUserTripsResponse, this, null, ServiceNames.GET_USER_TRIPS, false);
        list.execute(request);
    }


    private void setTripList(List<TripDto> list) {
        listData = new ArrayList<>();
        int start = list.size() - 1;
        for (int i = start; i >= 0; i--) {
            TripInfo tripInfo = new TripInfo();
            tripInfo.setId(list.get(i).getId());
            tripInfo.setPort(list.get(i).getSource());
            tripInfo.setDes(list.get(i).getDestination());
            tripInfo.setStartDate(list.get(i).getStartDate());
            tripInfo.setStartTime(list.get(i).getStartTime());
            tripInfo.setEndDate(list.get(i).getEndDate());
            tripInfo.setEndTime(list.get(i).getEndTime());
            tripInfo.setTrans(list.get(i).getTransport());
            listData.add(tripInfo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getTripList();
    }


}
