package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.PlaceAdapter;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.Place;
import com.ibm.hamsafar.object.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.CountryDto;
import hamsafar.ws.common.RegionDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.RegionListRequest;
import hamsafar.ws.request.RegionPlacesInfoRequest;
import hamsafar.ws.response.CountryListResponse;
import hamsafar.ws.response.RegionPlacesResponse;
import hamsafar.ws.response.RegionsListResponse;
import hamsafar.ws.util.service.PlaceType;
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

public class PlaceInfoActivity extends Activity {

    private Context context = this;
    private Button toolbar_back = null;
    private TextView toolbar_title = null;
    private EditText toolbar_search = null;
    private RecyclerView recyclerView = null;
    private PlaceAdapter adapter = null;
    private LinearLayoutManager linearLayoutManager = null;
    private List<Place> listData = null;
    private static String place_type = "tourism";
    private List<Region> country_list;
    private List<Region> region_list;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_info);

        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_text);
        toolbar_search = findViewById(R.id.toolbar_search );
        recyclerView = findViewById(R.id.place_info_recycler );

        listData = new ArrayList<>();

        toolbar_search.setFocusable( false );

        place_type = getIntent().getStringExtra("type");

        initialise();

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

        toolbar_search.setOnClickListener(view -> {
                if( place_type.equals("police") ) {
                    String title = getResources().getString(R.string.place_info_city_title);
                    showSearchListDialog(toolbar_search, title, getRegion_list());
                }
                else {
                    String title = getResources().getString(R.string.place_info_country_title);
                    showSearchListDialog(toolbar_search, title, getCountry_list());
                }
        });

        /*toolbar_search.setOnEditorActionListener((textView, i, keyEvent) -> {
            if( i == EditorInfo.IME_ACTION_SEARCH ) {
                getPlaceInfo();
                return true;
            }
            return false;
        });
*/
    }

    private void initialise() {
        getCityList();
        getCountryList();
    }


    //get city list from DB
    private void getCityList() {
        RegionListRequest request = new RegionListRequest();
        request.setCountryId(0);

        TaskCallBack<Object> regionsListResponse = result -> {
            RegionsListResponse ress = JsonCodec.toObject((Map) result, RegionsListResponse.class);
            List<RegionDto> regionDtoList = ress.getRegionDtoList();
            List<Region> list = new ArrayList<>();
            for( int i=0; i<regionDtoList.size(); i++ ) {
                Region region = new Region();
                region.setId( regionDtoList.get(i).getId() );
                region.setTitle( regionDtoList.get(i).getName() );
                list.add( region );
            }
            setRegion_list( list );
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(regionsListResponse, this, null, ServiceNames.REGION_LIST, false);
        list.execute(request);
    }

    //get country list from DB
    private void getCountryList() {
        RegionListRequest request = new RegionListRequest();

        TaskCallBack<Object> countryListResponse = result -> {
            CountryListResponse ress = JsonCodec.toObject((Map) result, CountryListResponse.class);
            List<CountryDto> regionDtoList = ress.getCountryDtoList();
            List<Region> list = new ArrayList<>();
            for( int i=0; i<regionDtoList.size(); i++ ) {
                Region region = new Region();
                region.setId( regionDtoList.get(i).getId() );
                region.setTitle( regionDtoList.get(i).getName() );
                list.add( region );
            }
            setCountry_list( list );
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(countryListResponse, this, null, ServiceNames.COUNTRY_LIST, false);
        list.execute(request);
    }


    //show list with search view as dialog
    private void showSearchListDialog(EditText editText, String title, List<Region> regions) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View listViewDialog = inflater.inflate(R.layout.list_with_search_view, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.dialog_toolbar_title);
        Button close = listViewDialog.findViewById(R.id.dialog_toolbar_close);
        ListView listView = listViewDialog.findViewById(R.id.listWithSearchList);
        EditText searchInput = listViewDialog.findViewById(R.id.listWithSearchInput);
        listTitle.setText(title);
        String[] items = extractRegionName( regions );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_expandable_list_item, items);
        listView.setAdapter(adapter);
        //adding search functionality to ListView using inputSearch
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String value = ((TextView) view).getText().toString();
            editText.setText(value);
            dialog.dismiss();
            getPlaceInfo();
        });
        close.setOnClickListener(view -> dialog.dismiss());
    }


    //get name of regions
    private String[] extractRegionName( List<Region> regions ) {
        String[] names = new String[regions.size()];
        for( int i=0; i<regions.size(); i++ ) {
            names[i] = regions.get(i).getTitle();
        }
        return names;
    }

    //get region id
    private int getRegionId( List<Region> regions, String value ) {
        boolean found = false;
        int index = 0;
        int i = 0;
        while( !found ) {
            if( regions.get(i).getTitle().equals( value ) ) {
                index = regions.get(i).getId();
                found = true;
            }
            i++;
        }
        return index;
    }

    public List<Region> getCountry_list() {
        return country_list;
    }

    public void setCountry_list(List<Region> country_list) {
        this.country_list = country_list;
    }


    public List<Region> getRegion_list() {
        return region_list;
    }

    public void setRegion_list(List<Region> region_list) {
        this.region_list = region_list;
    }


    private void getPlaceInfo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        RegionPlacesInfoRequest request = new RegionPlacesInfoRequest();
        request.setUserId( user_id );
        request.setRegionId( getRegionId( country_list, toolbar_search.getText().toString().trim()));

        switch ( place_type ) {
            case "tourism" :
                request.setPlaceType(PlaceType.SIGHTSEEING);
                request.setRegionId( getRegionId( country_list, toolbar_search.getText().toString().trim()));
                break;
            case "police" :
                request.setPlaceType(PlaceType.POLICE10PLUS);
                request.setRegionId( getRegionId( region_list, toolbar_search.getText().toString().trim()));
                break;
            case "hotel" :
                request.setPlaceType(PlaceType.RESIDENCY);
                request.setRegionId( getRegionId( country_list, toolbar_search.getText().toString().trim()));
                break;
            case "museum" :
                request.setPlaceType(PlaceType.RESTAURANT);
                request.setRegionId( getRegionId( country_list, toolbar_search.getText().toString().trim()));
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
