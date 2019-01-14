package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.Region;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.CountryDto;
import hamsafar.ws.common.RegionDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.RegionListRequest;
import hamsafar.ws.response.CountryListResponse;
import hamsafar.ws.response.RegionsListResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 1/12/2019.
 */
public class TripFilterActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    private Context context = this;
    private Button toolbar_back = null;
    private TextView toolbar_title = null;
    private Button toolbar_clear = null;
    private ImageButton topic_clear = null;
    private TextInputLayout topic_layout = null;
    private EditText topic = null;
    private ImageButton port_clear = null;
    private TextInputLayout port_layout = null;
    private EditText port = null;
    private ImageButton des_clear = null;
    private TextInputLayout des_layout = null;
    private EditText des = null;
    private ImageButton start_clear = null;
    private TextInputLayout start_layout = null;
    private EditText start = null;
    private ImageButton end_clear = null;
    private TextInputLayout end_layout = null;
    private EditText end = null;
    private Button done = null;

    private List<Region> region_list;
    private List<Region> country_list;
    private String DATE_PICKER_CALLER = "start";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_filter);

        toolbar_back = findViewById(R.id.trip_filter_toolbar_back );
        toolbar_title = findViewById(R.id.trip_filter_toolbar_text );
        toolbar_clear = findViewById(R.id.trip_filter_toolbar_clear );
        topic_clear = findViewById(R.id.trip_filter_topic_clr );
        topic_layout = findViewById(R.id.trip_filter_topic_layout );
        topic = findViewById(R.id.trip_filter_topic );
        port_clear = findViewById(R.id.trip_filter_port_clr );
        port_layout = findViewById(R.id.trip_filter_port_layout );
        port = findViewById(R.id.trip_filter_port );
        des_clear = findViewById(R.id.trip_filter_des_clr );
        des_layout = findViewById(R.id.trip_filter_des_layout );
        des = findViewById(R.id.trip_filter_des );
        start_clear = findViewById(R.id.trip_filter_start_clr );
        start_layout = findViewById(R.id.trip_filter_start_layout );
        start = findViewById(R.id.trip_filter_start );
        end_clear = findViewById(R.id.trip_filter_end_clr );
        end_layout = findViewById(R.id.trip_filter_end_layout );
        end = findViewById(R.id.trip_filter_end );
        done = findViewById(R.id.trip_filter_done);

        toolbar_back.setOnClickListener(view -> onBackPressed());

        toolbar_title.setText( getResources().getString( R.string.trip_filter_title ));

        toolbar_clear.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
            if(sharedPreferences.contains("trip_filter_exist") ) {
                String value = sharedPreferences.getString("trip_filter_exist", "");
                if( value.equals("yes") ) {
                    savePreferences("trip_filter_exist", "no");
                    savePreferences("trip_filter_topic", "");
                    savePreferences("trip_filter_port", "");
                    savePreferences("trip_filter_port_id", -1);
                    savePreferences("trip_filter_destination", "");
                    savePreferences("trip_filter_destination_id", -1);
                    savePreferences("trip_filter_start", "");
                    savePreferences("trip_filter_end", "");
                }
                Toast.makeText(context, getResources().getString(R.string.trip_filter_success_clear), Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        initialise();

        topic_layout.getEditText().addTextChangedListener(
                new TripFilterActivity.FilterTextWatcher(topic_layout.getEditText(), topic_layout, topic_clear));

        port_layout.getEditText().addTextChangedListener(
                new TripFilterActivity.FilterTextWatcher(port_layout.getEditText(), port_layout, port_clear));

        des_layout.getEditText().addTextChangedListener(
                new TripFilterActivity.FilterTextWatcher(des_layout.getEditText(), des_layout, des_clear));

        start_layout.getEditText().addTextChangedListener(
                new TripFilterActivity.FilterTextWatcher(start_layout.getEditText(), start_layout, start_clear));

        end_layout.getEditText().addTextChangedListener(
                new TripFilterActivity.FilterTextWatcher(end_layout.getEditText(), end_layout, end_clear));

        // clear buttons listener

        topic_clear.setOnClickListener(view -> {
            topic.getText().clear();
            topic_clear.setVisibility( View.GONE );
        });

        port_clear.setOnClickListener(view -> {
            port.getText().clear();
            port_clear.setVisibility( View.GONE );
        });

        des_clear.setOnClickListener(view -> {
            des.getText().clear();
            des_clear.setVisibility( View.GONE );
        });

        start_clear.setOnClickListener(view -> {
            start.getText().clear();
            start_clear.setVisibility( View.GONE );
            start_layout.setError( null );
            end_layout.setError( null );
        });

        end_clear.setOnClickListener(view -> {
            end.getText().clear();
            end_clear.setVisibility( View.GONE );
            end_layout.setError( null );
            start_layout.setError( null );
        });

        //EditTexts listener

        port.setOnClickListener(view -> {
                String title = getResources().getString(R.string.trip_port_title);
                showSearchListDialog(port, title, getRegion_list());
        });

        des.setOnClickListener(view -> {
                String title = getResources().getString(R.string.trip_des_title);
                showSearchListDialog(des, title, getCountry_list());
        });

        start.setOnClickListener(view -> {
            openDatePicker();
            DATE_PICKER_CALLER = "start";
        });

        end.setOnClickListener(view -> {
            openDatePicker();
            DATE_PICKER_CALLER = "end";
        });

        done.setOnClickListener(view -> {

            savePreferences("trip_filter_exist", "yes");
            savePreferences("trip_filter_topic", topic.getText().toString().trim() );
            savePreferences("trip_filter_port", port.getText().toString().trim());

            if( !port.getText().toString().trim().equals("") )
                savePreferences("trip_filter_port_id", getRegionId( region_list, port.getText().toString().trim()));
            if( port.getText().toString().trim().equals("") )
                savePreferences("trip_filter_port_id", -1);

            savePreferences("trip_filter_destination", des.getText().toString().trim() );

            if( !des.getText().toString().trim().equals("") )
                savePreferences("trip_filter_destination_id", getRegionId(country_list, des.getText().toString().trim()));
            if( des.getText().toString().trim().equals("") )
                savePreferences("trip_filter_destination_id", -1);

            savePreferences("trip_filter_start", start.getText().toString().trim() );
            savePreferences("trip_filter_end", end.getText().toString().trim() );

            finish();
        });

    }


    private void initialise() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        //initialise texts
        if( sharedPreferences.contains("trip_filter_exist") ) {
            String exist = sharedPreferences.getString("trip_filter_exist", "");
            if( exist.equals("yes") ) {

                String topic_str = sharedPreferences.getString("trip_filter_topic", "");
                if( !topic_str.equals("") ) {
                    topic.setText( topic_str );
                    topic_clear.setVisibility( View.VISIBLE );
                }
                if( topic_str.equals("") ) {
                    topic_clear.setVisibility( View.GONE );
                }

                String port_str = sharedPreferences.getString("trip_filter_port", "");
                if( !port_str.equals("") ) {
                    port.setText( port_str );
                    port_clear.setVisibility( View.VISIBLE );
                }
                if( port_str.equals("") ) {
                    port_clear.setVisibility( View.GONE );
                }

                String des_str = sharedPreferences.getString("trip_filter_destination", "");
                if( !des_str.equals("") ) {
                    des.setText( des_str );
                    des_clear.setVisibility( View.VISIBLE );
                }
                if( des_str.equals("") ) {
                    des_clear.setVisibility( View.GONE );
                }


                String start_str = sharedPreferences.getString("trip_filter_start", "");
                if( !start_str.equals("") ) {
                    start.setText( start_str );
                    start_clear.setVisibility( View.VISIBLE );
                }
                if( start_str.equals("") ) {
                    start_clear.setVisibility( View.GONE );
                }

                String end_str = sharedPreferences.getString("trip_filter_end", "");
                if( !end_str.equals("") ) {
                    end.setText( end_str );
                    end_clear.setVisibility( View.VISIBLE );
                }
                if( end_str.equals("") ) {
                    end_clear.setVisibility( View.GONE );
                }
            }
            else {
                topic_clear.setVisibility(View.GONE);
                port_clear.setVisibility(View.GONE);
                des_clear.setVisibility(View.GONE);
                start_clear.setVisibility(View.GONE);
                end_clear.setVisibility(View.GONE);
            }
        }
        else {
            topic_clear.setVisibility(View.GONE);
            port_clear.setVisibility(View.GONE);
            des_clear.setVisibility(View.GONE);
            start_clear.setVisibility(View.GONE);
            end_clear.setVisibility(View.GONE);
        }

        region_list = new ArrayList<>();
        country_list = new ArrayList<>();

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

    public List<Region> getRegion_list() {
        return region_list;
    }

    public void setRegion_list(List<Region> region_list) {
        this.region_list = region_list;
    }

    public List<Region> getCountry_list() {
        return country_list;
    }

    public void setCountry_list(List<Region> country_list) {
        this.country_list = country_list;
    }


    //show list with search view as dialog
    private void showSearchListDialog(EditText editText, String title, List<Region> regions) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TripFilterActivity.this);
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

    //open date picker dialog
    private void openDatePicker() {
        PersianCalendar persianCalendar = new PersianCalendar();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                TripFilterActivity.this,
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onDateSet(com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int moth = monthOfYear + 1;
        //set date
        if (DATE_PICKER_CALLER.equals("start")) {
            start.setText(year + "/" + String.format("%02d", moth) + "/" + String.format("%02d", dayOfMonth));
        }
        if (DATE_PICKER_CALLER.equals("end")) {
            end.setText(year + "/" + String.format("%02d", moth) + "/" + String.format("%02d", dayOfMonth));
        }

        //check date validation
        String start_st = start.getText().toString();
        String end_st = end.getText().toString();
        Date startDate = null;
        Date endDate = null;
        if (!start_st.equals("") && !end_st.equals("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                startDate = simpleDateFormat.parse(start_st);
                endDate = simpleDateFormat.parse(end_st);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (startDate.after(endDate)) {
                if (DATE_PICKER_CALLER.equals("start")) {
                    start_layout.setError(getResources().getString(R.string.trip_start_after_end_exc));
                    end_layout.setError(null);
                } else {
                    end_layout.setError(getResources().getString(R.string.trip_end_before_start_exc));
                    start_layout.setError(null);
                }
            } else {
                start_layout.setError(null);
                end_layout.setError(null);
            }
        }
    }

    //text watcher inner class

    static class FilterTextWatcher implements TextWatcher {

        private View view;
        private TextInputLayout layout;
        private ImageButton clear;

        FilterTextWatcher(View view, TextInputLayout layout, ImageButton clear) {
            this.view = view;
            this.layout = layout;
            this.clear = clear;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(s.toString().trim().length() == 0 ) {
                clear.setVisibility( View.GONE );
            }
            else {
                clear.setVisibility( View.VISIBLE );
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                clear.setVisibility(View.VISIBLE);
            }
            else {
                clear.setVisibility( View.GONE );
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length() > 0) {
                clear.setVisibility(View.VISIBLE);
            }
            else {
                clear.setVisibility( View.GONE );
            }
        }
    }


    private void showListDialog(final EditText editText, String title, String[] items) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TripFilterActivity.this);
        builder.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View listViewDialog = inflater.inflate(R.layout.alertdialog_with_list, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.listAlertDialogTitle);
        listTitle.setText(title);
        ListView listView = listViewDialog.findViewById(R.id.dialogList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_expandable_list_item, items);
        listView.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String value = ((TextView) view).getText().toString();
            editText.setText(value);
            dialog.dismiss();
        });
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


    private void savePreferences(String key, Integer value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
