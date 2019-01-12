package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.hamsafar.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.Region;
import com.ibm.hamsafar.object.Transport;
import com.ibm.hamsafar.object.TripInfo;
import com.ibm.hamsafar.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.CountryDto;
import hamsafar.ws.common.RegionDto;
import hamsafar.ws.common.VehicleDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.RegionListRequest;
import hamsafar.ws.request.SubmitTripRequest;
import hamsafar.ws.response.CountryListResponse;
import hamsafar.ws.response.RegionsListResponse;
import hamsafar.ws.response.SubmitTripResponse;
import hamsafar.ws.response.VehiclesListResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TripActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private TextInputLayout portLayout = null;
    private EditText portText = null;
    private TextInputLayout desLayout = null;
    private EditText desText = null;
    private TextInputLayout startDateLayout = null;
    private EditText startDateText = null;
    private TextInputLayout startTimeLayout = null;
    private EditText startTimeText = null;
    private TextInputLayout endDateLayout = null;
    private EditText endDateText = null;
    private TextInputLayout endTimeLayout = null;
    private EditText endTimeText = null;
    private TextInputLayout transLayout = null;
    private EditText transText = null;
    private Button save = null;
    private Switch checkList = null;
    private LinearLayout parentLayout = null;
    private String DATE_PICKER_CALLER = "start";
    private String TIME_PICKER_CALLER = "start";
    private TripInfo tripInfo = new TripInfo();
    private List<Region> region_list;
    private List<Region> country_list;
    private List<Transport> transport_list;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        portLayout = findViewById(R.id.trip_port_layout);
        portText = findViewById(R.id.trip_port);
        desLayout = findViewById(R.id.trip_destination_layout);
        desText = findViewById(R.id.trip_destination);
        startDateLayout = findViewById(R.id.trip_start_date_layout);
        startDateText = findViewById(R.id.trip_start_date);
        startTimeLayout = findViewById(R.id.trip_start_time_layout);
        startTimeText = findViewById(R.id.trip_start_time);
        endDateLayout = findViewById(R.id.trip_end_date_layout);
        endDateText = findViewById(R.id.trip_end_date);
        endTimeLayout = findViewById(R.id.trip_end_time_layout);
        endTimeText = findViewById(R.id.trip_end_time);
        transLayout = findViewById(R.id.trip_transport_layout);
        transText = findViewById(R.id.trip_transport);
        save = findViewById(R.id.trip_save_btn);
        checkList = findViewById(R.id.trip_check_list_switch);
        parentLayout = findViewById(R.id.trip_parent);


        toolbarTitle.setText(getResources().getString(R.string.trip_title));

        toolbarBack.setOnClickListener(view -> onBackPressed());

        region_list = new ArrayList<>();
        transport_list = new ArrayList<>();

        clearError();

        portLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(portLayout.getEditText(), portLayout));
        desLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(desLayout.getEditText(), desLayout));
        startDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startDateLayout.getEditText(), startDateLayout));
        startTimeLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startDateLayout.getEditText(), startDateLayout));
        endDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(endDateLayout.getEditText(), endDateLayout));
        endTimeLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(endDateLayout.getEditText(), endDateLayout));
        transLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(transLayout.getEditText(), transLayout));


        initialise();

        portText.setOnClickListener(view -> {
            if (checkInternetConnection()) {
                String title = getResources().getString(R.string.trip_port_title);
                showSearchListDialog(portText, title, getRegion_list());
            } else {
                Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        });

        desText.setOnClickListener(view -> {
            if (checkInternetConnection()) {
                String title = getResources().getString(R.string.trip_des_title);
                showSearchListDialog(desText, title, getCountry_list());
            } else {
                Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        });

        startDateText.setOnClickListener(view -> {
            openDatePicker();
            DATE_PICKER_CALLER = "start";
        });

        endDateText.setOnClickListener(view -> {
            openDatePicker();
            DATE_PICKER_CALLER = "end";
        });


        startTimeText.setOnClickListener(view -> {
            openTimePicker();
            TIME_PICKER_CALLER = "start";
        });

        endTimeText.setOnClickListener(view -> {
            openTimePicker();
            TIME_PICKER_CALLER = "end";
        });


        transText.setOnClickListener(view -> {
            String title = getResources().getString(R.string.trip_trans_title);
            showListDialog(transText, title, extractTransportName( getTransport_list() ));
        });



        save.setOnClickListener(view -> {
            saveAction(view);
        });


    }

    private void initialise() {
        getCityList();
        getCountryList();
        getTransportList();

        //get user location
        //portText.setText(region_list.get(0).getTitle());

        //set now for start date
        startDateText.setText(DateUtil.getCurrentDate());

        //transText.setText(transport_list.get(0).getName());

        desText.setText("");
        endDateText.setText("");
    }

    //save button action
    private void saveAction(View view) {
        boolean hasError = false;

        if (desText.getText().toString().trim().equals("")) {
            desLayout.setError(getResources().getString(R.string.Exc_800001));
            hasError = true;
        }

        if (endDateText.getText().toString().trim().equals("")) {
            endDateLayout.setError(getResources().getString(R.string.Exc_800002));
            hasError = true;
        }

        if (hasError) {
            Snackbar.make(view, getResources().getString(R.string.Exc_700007), Snackbar.LENGTH_SHORT).show();
        } else {
            if (checkInternetConnection()) {
                clearError();
                insertTripIntoDB();
            } else {
                Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
     * show list dialog
     *
     * */
    private void showListDialog(final EditText editText, String title, String[] items) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);
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


    private void setTripInfo( Integer id ) {
        tripInfo.setId( id );
        tripInfo.setPort(portText.getText().toString().trim());
        tripInfo.setDes(desText.getText().toString().trim());
        tripInfo.setStartDate(startDateText.getText().toString().trim());
        tripInfo.setStartTime( startTimeText.getText().toString().trim() );
        tripInfo.setEndDate(endDateText.getText().toString().trim());
        tripInfo.setEndTime( endTimeText.getText().toString().trim() );
        tripInfo.setTrans(transText.getText().toString().trim());
    }


    /*
     * insert trip info into DB
     * get its id
     * pass trip info to checklist activity if needed( contains trip id )
     * */

    private void insertTripIntoDB() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        Integer user_id = sharedPreferences.getInt("user_id", 0 );

        SubmitTripRequest request = new SubmitTripRequest();
        request.setUserId( user_id );
        request.setTitle(portText.getText().toString() + " " + startDateText.getText().toString());
        request.setSourceId(getRegionId( region_list, portText.getText().toString().trim()));
        request.setDestinationId(getRegionId( country_list, desText.getText().toString().trim()));
        request.setStartDate( startDateText.getText().toString().trim() );
        request.setStartTime( startTimeText.getText().toString().trim() );
        request.setEndDate( endDateText.getText().toString().trim() );
        request.setEndTime( endTimeText.getText().toString().trim() );
        request.setTripType((byte) 0);
        request.setTripStatus((byte) 0);
        request.setTransportId((byte) getTransportId( transport_list, transText.getText().toString().trim() ) );

        TaskCallBack<Object> submitTripResponse = result -> {
            SubmitTripResponse ress = JsonCodec.toObject((Map) result, SubmitTripResponse.class);

            Intent intent;
            setTripInfo( ress.getTripId() );
            if (checkList.isChecked()) {
                intent = new Intent(TripActivity.this, CheckListActivity.class);
                intent.putExtra("trip_info", tripInfo);
            } else {
                intent = new Intent(TripActivity.this, Main.class);
                Toast.makeText(context, getResources().getString(R.string.trip_save_message), Toast.LENGTH_SHORT).show();
            }
            startActivity(intent);
            finish();

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitTripResponse, this, null, ServiceNames.SUBMIT_TRIP, false);
        list.execute(request);
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

    //get transport id
    private int getTransportId( List<Transport> transports, String value ) {
        boolean found = false;
        int index = 0;
        int i = 0;
        while( !found ) {
            if( transports.get(i).getName().equals( value ) ) {
                index = transports.get(i).getId();
                found = true;
            }
            i++;
        }
        return index;
    }


    class GenericTextWatcher implements TextWatcher {

        private View view;
        private TextInputLayout layout;

        private GenericTextWatcher(View view, TextInputLayout layout) {
            this.view = view;
            this.layout = layout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0)
                layout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
            layout.setError(null);
        }
    }

    private void clearError() {
        portLayout.setError(null);
        desLayout.setError(null);
        startDateLayout.setError(null);
        endDateLayout.setError(null);
        transLayout.setError(null);
    }


    private boolean checkInternetConnection() {
        //if connected return true
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable() || !info.isConnected()) {
            return false;
        }
        return true;
    }

    //open date picker dialog
    private void openDatePicker() {
        PersianCalendar persianCalendar = new PersianCalendar();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                TripActivity.this,
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
            startDateText.setText(year + "/" + moth + "/" + dayOfMonth);
        }
        if (DATE_PICKER_CALLER.equals("end")) {
            endDateText.setText(year + "/" + moth + "/" + dayOfMonth);
        }

        //check date validation
        String start = startDateText.getText().toString();
        String end = endDateText.getText().toString();
        Date startDate = null;
        Date endDate = null;
        if (!start.equals("") && !end.equals("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                startDate = simpleDateFormat.parse(start);
                endDate = simpleDateFormat.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (startDate.after(endDate)) {
                if (DATE_PICKER_CALLER.equals("start")) {
                    startDateLayout.setError(getResources().getString(R.string.trip_start_after_end_exc));
                    endDateLayout.setError(null);
                } else {
                    endDateLayout.setError(getResources().getString(R.string.trip_end_before_start_exc));
                    startDateLayout.setError(null);
                }
            } else {
                startDateLayout.setError(null);
                endDateLayout.setError(null);
            }
        }
    }


    //open time picker
    private void openTimePicker() {
        PersianCalendar now = new PersianCalendar();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                TripActivity.this,
                now.get(PersianCalendar.HOUR_OF_DAY),
                now.get(PersianCalendar.MINUTE),
                true
        );
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = hourString + ":" + minuteString;
        if( TIME_PICKER_CALLER.equals("start")) {
            this.startTimeText.setText(time);
        }
        if( TIME_PICKER_CALLER.equals("end")) {
            this.endTimeText.setText(time);
        }
    }


    //show list with search view as dialog
    private void showSearchListDialog(EditText editText, String title, List<Region> regions) {
        /*final AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);
        builder.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View listViewDialog = inflater.inflate(R.layout.list_with_search_view, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.dialog_toolbar_title);
        Button close = listViewDialog.findViewById(R.id.dialog_toolbar_close);
        RecyclerView list = listViewDialog.findViewById(R.id.listWithSearchList);
        EditText searchInput = listViewDialog.findViewById(R.id.listWithSearchInput);
        listTitle.setText(title);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TripActivity.this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        RegionAdapter adapter = new RegionAdapter(this, items);
        list.setAdapter(adapter);
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
        list.setOnItemClickListener((parent, view, position, id) -> {
            String value = ((TextView) view).getText().toString();
            editText.setText(value);
            dialog.dismiss();
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });*/

        final AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);
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


    //get name of transports
    private String[] extractTransportName( List<Transport> transports ) {
        String[] names = new String[transports.size()];
        for( int i=0; i<transports.size(); i++ ) {
            names[i] = transports.get(i).getName();
        }
        return names;
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

    //get transport list from DB
    private void getTransportList() {
        TaskCallBack<Object> vehiclesListResponse = result -> {
            VehiclesListResponse ress = JsonCodec.toObject((Map) result, VehiclesListResponse.class);
            List<VehicleDto> vehicleDtos = ress.getVehicleDtoList();
            List<Transport> list = new ArrayList<>();
            for( int i=0; i<vehicleDtos.size(); i++ ) {
                Transport transport = new Transport();
                transport.setId( vehicleDtos.get(i).getId() );
                transport.setName( vehicleDtos.get(i).getDescription() );
                list.add( transport );
            }
            setTransport_list( list );
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(vehiclesListResponse, this, null, ServiceNames.VEHICLE_LIST, false);
        list.execute();
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

    public List<Transport> getTransport_list() {
        return transport_list;
    }

    public void setTransport_list(List<Transport> transport_list) {
        this.transport_list = transport_list;
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TripActivity.this);
        builder.setMessage(getResources().getString(R.string.exit_message));
        builder.setPositiveButton(getResources().getString(R.string.exit_save_changes),
                (dialogInterface, i) -> {
                    saveAction(parentLayout);
                });
        builder.setNegativeButton(getResources().getString(R.string.exit_cancel),
                (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
        builder.setNeutralButton(getResources().getString(R.string.exit_discard),
                (dialogInterface, i) -> {
                    finish();
                });
        builder.show();
    }

}
