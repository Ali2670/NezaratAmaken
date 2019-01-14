package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.PlaceAdapter;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.RegionPlacesInfoRequest;
import hamsafar.ws.response.RegionPlacesResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 1/12/2019.
 */


/*
*
* place type = tourism, police, hotel, museum
*
* */

public class SimplePlaceInfoActivity extends Activity {

    private Context context = this;
    private Button toolbar_back = null;
    private TextView toolbar_title = null;
    private RecyclerView recyclerView = null;
    private PlaceAdapter adapter = null;
    private LinearLayoutManager linearLayoutManager = null;
    private List<Place> listData = null;
    private static String place_type = "tourism";
    private int region_id = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_place_info);

        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_text);
        recyclerView = findViewById(R.id.place_info_recycler );

        listData = new ArrayList<>();


        place_type = getIntent().getStringExtra("type");

        region_id = getIntent().getIntExtra("region_id", 0);

        toolbar_back.setOnClickListener(view -> onBackPressed());

        switch ( place_type ) {
            case "tourism" :
                toolbar_title.setText(getResources().getString(R.string.tourism_info_title));
                break;
            case "police" :
                toolbar_title.setText(getResources().getString(R.string.police_ten_title));
                break;
            case "hotel" :
                toolbar_title.setText(getResources().getString(R.string.hotel_info_title));
                break;
            case "museum" :
                toolbar_title.setText(getResources().getString(R.string.museum_info_title));
                break;
        }


            getPlaceInfo();
    }


    private void getPlaceInfo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        RegionPlacesInfoRequest request = new RegionPlacesInfoRequest();
        request.setUserId( user_id );
        request.setRegionId( region_id);

        switch ( place_type ) {
            case "tourism" :
                //request.setPlaceType(new PlaceType((byte) 0));
                break;
            case "police" :

                break;
            case "hotel" :

                break;
            case "museum" :

                break;
        }

        TaskCallBack<Object> regionPlacesResponse = result -> {
            RegionPlacesResponse ress = JsonCodec.toObject((Map) result, RegionPlacesResponse.class);

            //fill list data
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new PlaceAdapter(this, listData);
            recyclerView.setAdapter(adapter);

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(regionPlacesResponse, this, null, ServiceNames.PLACE_INFO, false);
        list.execute(request);
    }
}
