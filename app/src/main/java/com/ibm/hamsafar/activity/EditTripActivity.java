package com.ibm.hamsafar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.hamsafar.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.CheckItemAdapter;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.CheckItem;
import com.ibm.hamsafar.object.Region;
import com.ibm.hamsafar.object.Transport;
import com.ibm.hamsafar.object.TripInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.ChecklistItemDto;
import hamsafar.ws.common.CountryDto;
import hamsafar.ws.common.RegionDto;
import hamsafar.ws.common.ReminderDto;
import hamsafar.ws.common.VehicleDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.GetChecklistRequest;
import hamsafar.ws.request.RegionListRequest;
import hamsafar.ws.request.SubmitTripChecklistRequest;
import hamsafar.ws.request.SubmitTripRequest;
import hamsafar.ws.response.CountryListResponse;
import hamsafar.ws.response.GetChecklistResponse;
import hamsafar.ws.response.RegionsListResponse;
import hamsafar.ws.response.SubmitChecklistResponse;
import hamsafar.ws.response.SubmitTripResponse;
import hamsafar.ws.response.VehiclesListResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/24/2018.
 */
public class EditTripActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private Context context = this;
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
    /*private Switch addChecklist = null;
    private Switch showChecklist = null;*/
    private Button done = null;
    private Button cancel = null;
    private RecyclerView checklist = null;
    private FloatingActionButton addItem = null;

    private LinearLayoutManager linearLayoutManager = null;
    private List<CheckItem> listData = null;
    //private UnEditableCheckListAdapter adapter = null;
    private CheckItemAdapter adapter = null;

    private String DATE_PICKER_CALLER = "start";
    private String TIME_PICKER_CALLER = "start";
    private TripInfo tripInfo = new TripInfo();
    private static Integer trip_id = 0;
    private static boolean has_checklist = false;


    private List<Region> region_list;
    private List<Region> country_list;
    private List<Transport> transport_list;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.trip_edit);

        portLayout = findViewById(R.id.trip_edit_port_layout);
        portText = findViewById(R.id.trip_edit_port);
        desLayout = findViewById(R.id.trip_edit_destination_layout);
        desText = findViewById(R.id.trip_edit_destination);
        startDateLayout = findViewById(R.id.trip_edit_start_date_layout);
        startDateText = findViewById(R.id.trip_edit_start_date);
        startTimeLayout = findViewById(R.id.trip_edit_start_time_layout);
        startTimeText = findViewById(R.id.trip_edit_start_time);
        endDateLayout = findViewById(R.id.trip_edit_end_date_layout);
        endDateText = findViewById(R.id.trip_edit_end_date);
        endTimeLayout = findViewById(R.id.trip_edit_end_time_layout);
        endTimeText = findViewById(R.id.trip_edit_end_time);
        transLayout = findViewById(R.id.trip_edit_transport_layout);
        transText = findViewById(R.id.trip_edit_transport);
        done = findViewById(R.id.toolbar_save);
        cancel = findViewById(R.id.toolbar_cancel);
        /*addChecklist = findViewById(R.id.trip_edit_check_list_add);
        showChecklist = findViewById(R.id.trip_edit_check_list_show);*/
        checklist = findViewById(R.id.trip_edit_checklist);
        addItem = findViewById(R.id.trip_edit_checklist_add);


        clearError();

        portLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(portLayout.getEditText(), portLayout));
        desLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(desLayout.getEditText(), desLayout));
        startDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startDateLayout.getEditText(), startDateLayout));
        startTimeLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startTimeLayout.getEditText(), startTimeLayout));
        endDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(endDateLayout.getEditText(), endDateLayout));
        endTimeLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(endTimeLayout.getEditText(), endTimeLayout));
        transLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(transLayout.getEditText(), transLayout));


        getCityList();
        getCountryList();
        getTransportList();


        if (getIntent().hasExtra("trip")) {
            tripInfo = (TripInfo) getIntent().getSerializableExtra("trip");
            trip_id = tripInfo.getId();
            loadChecklist();
        }


        /*
         *
         * check trip info
         * if it has checklist then show in recyclerView and make showChecklist visible
         * else make addChecklist visible to choose whether to define one or not
         *
         * have checklist -> show card visible and switch gone
         *
         */
        /*addChecklist.setVisibility( View.GONE );
        showChecklist.setVisibility( View.GONE );*/
        checklist.setVisibility(View.GONE);
        addItem.setVisibility(View.GONE);

        /*if (has_checklist) {
            showChecklist.setVisibility(View.VISIBLE);
        }
        if (!has_checklist) {
            addChecklist.setVisibility(View.VISIBLE);
        }*/

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
            showListDialog(transText, title, extractTransportName(getTransport_list()));
        });

        /*showChecklist.setOnClickListener(view -> {
            if (showChecklist.isChecked()) {
                checklist.setVisibility(View.VISIBLE);
                addItem.setVisibility( View.VISIBLE );
                linearLayoutManager = new LinearLayoutManager(EditTripActivity.this, LinearLayoutManager.VERTICAL, false);
                checklist.setLayoutManager(linearLayoutManager);
                adapter = new CheckItemAdapter(this, listData);
                checklist.setAdapter(adapter);
            } else {
                checklist.setVisibility(View.GONE);
                addItem.setVisibility( View.GONE );
            }
        });*/


        done.setOnClickListener(view -> {
            boolean hasError = false;

            if (desText.getText().toString().trim().equals("")) {
                desLayout.setError(getResources().getString(R.string.Exc_800001));
                hasError = true;
            }

            if (endDateText.getText().toString().trim().equals("")) {
                endDateLayout.setError(getResources().getString(R.string.Exc_800002));
                hasError = true;
            }

            if (endTimeText.getText().toString().trim().equals("")) {
                endTimeLayout.setError(getResources().getString(R.string.Exc_800003));
                hasError = true;
            }

            if (startTimeText.getText().toString().trim().equals("")) {
                startTimeLayout.setError(getResources().getString(R.string.Exc_800004));
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
        });

        cancel.setOnClickListener(view -> finish());

        addItem.setOnClickListener(view -> {
            Intent intent = new Intent(EditTripActivity.this, CheckListItemEditActivity.class);
            intent.putExtra("trip_id", trip_id );
            startActivity(intent);
        });


    }


    //get trip and checklist info from DB
    /*private void loadTrip() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        GetUserTripsRequest request = new GetUserTripsRequest();
        request.setUserId(user_id);

        TaskCallBack<Object> getUserTripsResponse = result -> {
            GetUserTripsResponse ress = JsonCodec.toObject((Map) result, GetUserTripsResponse.class);
            if (ress.getTripDtoList().size() != 0) {
                List<TripDto> trip_list;
                trip_list = ress.getTripDtoList();
                loadTripInfo(trip_list);
            } else {
                Toast.makeText(context, "no trip", Toast.LENGTH_SHORT).show();
            }
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(getUserTripsResponse, this, null, ServiceNames.GET_USER_TRIPS, false);
        list.execute(request);
    }*/

    //get checklist info
    private void loadChecklist() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        GetChecklistRequest request = new GetChecklistRequest();
        request.setUserId(user_id);
        request.setTripId(trip_id);

        @SuppressLint("RestrictedApi")
        TaskCallBack<Object> getChecklistResponse = result -> {
            GetChecklistResponse ress = JsonCodec.toObject((Map) result, GetChecklistResponse.class);
            List<ChecklistItemDto> listDB;
            listDB = ress.getChecklistItemDtoList();

            //set trip info into views
            loadTripInfo();


            if (ress.getChecklistItemDtoList().size() != 0) {
                has_checklist = true;
                checklist.setVisibility(View.VISIBLE);
                addItem.setVisibility(View.VISIBLE);
                setListData(listDB);
                linearLayoutManager = new LinearLayoutManager(EditTripActivity.this, LinearLayoutManager.VERTICAL, false);
                checklist.setLayoutManager(linearLayoutManager);
                adapter = new CheckItemAdapter(this, listData);
                checklist.setAdapter(adapter);
            } else {
                has_checklist = false;
                checklist.setVisibility(View.GONE);
                addItem.setVisibility(View.GONE);
            }

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(getChecklistResponse, this, null, ServiceNames.GET_CHECKLIST, false);
        list.execute(request);
    }


    //fill trip list
    private void loadTripInfo() {
        portText.setText(tripInfo.getPort());
        desText.setText(tripInfo.getDes());
        startDateText.setText(tripInfo.getStartDate());
        startTimeText.setText(tripInfo.getStartTime());
        endDateText.setText(tripInfo.getEndDate());
        endTimeText.setText(tripInfo.getEndTime());
        transText.setText(tripInfo.getTrans());
    }


    //fill list using checklist info
    private void setListData(List<ChecklistItemDto> listDB) {
        listData = new ArrayList<>();
        for (int i = 0; i < listDB.size(); i++) {
            CheckItem item = new CheckItem();
            item.setTopic(listDB.get(i).getTitle());
            item.setId(listDB.get(i).getId());
            item.setDate("");
            item.setTime("");
            item.setTripId(listDB.get(i).getTripId());
            int statue = listDB.get(i).getStatus();
            if (statue == 0) {
                item.setChecked(false);
            } else
                item.setChecked(true);

            item.setReminderFlag(listDB.get(i).getReminderFlag());

            if (listDB.get(i).getReminderDto() != null) {
                item.setReminderDto(listDB.get(i).getReminderDto());
                item.setDate(listDB.get(i).getReminderDto().getRemindDate());
                item.setTime(listDB.get(i).getReminderDto().getRemindTime());
            }

            listData.add(item);
        }
    }


    /*
     * show list dialog
     *
     * */
    private void showListDialog(final EditText editText, String title, String[] items) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditTripActivity.this);
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


    /*
     * insert trip info into DB
     * also update the check box value of check items if a checklist exists
     * */

    private void insertTripIntoDB() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        SubmitTripRequest request = new SubmitTripRequest();
        request.setUserId(user_id);
        request.setTripId(trip_id);
        request.setTitle(portText.getText().toString() + " " + startDateText.getText().toString());
        request.setSourceId(getRegionId(region_list, portText.getText().toString().trim()));
        request.setDestinationId(getRegionId(country_list, desText.getText().toString().trim()));
        request.setStartDate(startDateText.getText().toString().trim());
        request.setStartTime(startTimeText.getText().toString().trim());
        request.setEndDate(endDateText.getText().toString().trim());
        request.setEndTime(endTimeText.getText().toString().trim());
        request.setTripType((byte) 0);
        request.setTripStatus((byte) 0);
        request.setTransportId((byte) getTransportId(transport_list, transText.getText().toString().trim()));

        TaskCallBack<Object> submitTripResponse = result -> {
            SubmitTripResponse ress = JsonCodec.toObject((Map) result, SubmitTripResponse.class);
            if (has_checklist) {
                updateChecklistInfo();
            }
            if (!has_checklist) {
                /*if (addChecklist.isChecked()) {*/
                updateTripInfo();
                Intent intent = new Intent(EditTripActivity.this, CheckListActivity.class);
                intent.putExtra("trip_info", tripInfo);
                startActivity(intent);
                finish();
                /*} else {
                    Intent intent = getIntent();
                    finish();
                    startActivity( intent );
                }*/
            }

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitTripResponse, this, null, ServiceNames.SUBMIT_TRIP, false);
        list.execute(request);
    }


    //save checklist into db
    private void updateChecklistInfo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        SubmitTripChecklistRequest request = new SubmitTripChecklistRequest();
        request.setUserId(user_id);
        request.setTripId(trip_id);

        //fill items
        request.setChecklistDtoList(fillDBList());

        TaskCallBack<Object> submitChecklistResponse = result -> {
            SubmitChecklistResponse ress = JsonCodec.toObject((Map) result, SubmitChecklistResponse.class);

            if (ress.getSuccessful()) {
                Toast.makeText(context, getResources().getString(R.string.save_success_message), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, getResources().getString(R.string.save_failure_message), Toast.LENGTH_SHORT).show();
            }

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitChecklistResponse, this, null, ServiceNames.SUBMIT_CHECKLIST, false);
        list.execute(request);
    }

    //fill DB list
    private List<ChecklistItemDto> fillDBList() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int user_id = sharedPreferences.getInt("user_id", 0);
        List<ChecklistItemDto> listDB = new ArrayList<>();

        for (CheckItem checkItem : listData) {
            ChecklistItemDto checklistItemDto = new ChecklistItemDto();
            checklistItemDto.setUserId(user_id);
            checklistItemDto.setTripId(trip_id);
            int item_id = checkItem.getId();
            if (item_id != 0) {
                checklistItemDto.setId(item_id);
            }
            checklistItemDto.setTitle(checkItem.getTopic());
            checklistItemDto.setFixedTitle(checkItem.getTopic());

            if (checkItem.isChecked())
                checklistItemDto.setStatus((byte) 1);
            if (!checkItem.isChecked())
                checklistItemDto.setStatus((byte) 0);

            String date_st = checkItem.getDate();
            String time_st = checkItem.getTime();
            if (!date_st.equals("") && !time_st.equals("")) {

                checklistItemDto.setReminderFlag((byte) 1);

                //create reminder
                ReminderDto reminderDto = new ReminderDto();
                reminderDto.setStartDate(date_st);
                //reminderDto.setStartDate(DateUtil.getCurrentDate());
                reminderDto.setStartTime(time_st);
                reminderDto.setReminderType((byte) 0);
                reminderDto.setRemindDate(date_st);
                //reminderDto.setRemindDate( DateUtil.getCurrentDate());
                reminderDto.setRemindTime(time_st);
                reminderDto.setStatus((byte) 0);

                checklistItemDto.setReminderDto(reminderDto);
            } else {
                checklistItemDto.setReminderFlag((byte) 0);
            }

            listDB.add(checklistItemDto);
        }
        return listDB;
    }


    private void setTripInfo(Integer id) {
        tripInfo.setId(id);
        tripInfo.setPort(portText.getText().toString().trim());
        tripInfo.setDes(desText.getText().toString().trim());
        tripInfo.setStartDate(startDateText.getText().toString().trim());
        tripInfo.setEndDate(endDateText.getText().toString().trim());
        tripInfo.setTrans(transText.getText().toString().trim());
    }

    private void updateTripInfo() {
        tripInfo.setId( trip_id );
        tripInfo.setPort( portText.getText().toString().trim() );
        tripInfo.setDes( desText.getText().toString().trim() );
        tripInfo.setStartDate( startDateText.getText().toString().trim() );
        tripInfo.setStartTime( startTimeText.getText().toString().trim() );
        tripInfo.setEndDate( endDateText.getText().toString().trim() );
        tripInfo.setEndTime( endTimeText.getText().toString().trim() );
        tripInfo.setTrans( transText.getText().toString().trim() );
    }


    //get region id
    private int getRegionId(List<Region> regions, String value) {
        boolean found = false;
        int index = 0;
        int i = 0;
        while (!found) {
            if (regions.get(i).getTitle().equals(value)) {
                index = regions.get(i).getId();
                found = true;
            }
            i++;
        }
        return index;
    }

    //get transport id
    private int getTransportId(List<Transport> transports, String value) {
        boolean found = false;
        int index = 0;
        int i = 0;
        while (!found) {
            if (transports.get(i).getName().equals(value)) {
                index = transports.get(i).getId();
                found = true;
            }
            i++;
        }
        return index;
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = hourString + ":" + minuteString;
        if (TIME_PICKER_CALLER.equals("start")) {
            this.startTimeText.setText(time);
        }
        if (TIME_PICKER_CALLER.equals("end")) {
            this.endTimeText.setText(time);
        }
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
        startTimeLayout.setError(null);
        endDateLayout.setError(null);
        endTimeLayout.setError(null);
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

    private TripInfo getTripInfo() {
        TripInfo trip = new TripInfo();
        trip.setPort(portText.getText().toString().trim());
        trip.setDes(desText.getText().toString().trim());
        trip.setStartDate(startDateText.getText().toString().trim());
        trip.setEndDate(endDateText.getText().toString().trim());
        trip.setTrans(transText.getText().toString().trim());
        return trip;
    }

    //open date picker dialog
    private void openDatePicker() {
        PersianCalendar persianCalendar = new PersianCalendar();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                EditTripActivity.this,
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }


    //open time picker
    private void openTimePicker() {
        PersianCalendar now = new PersianCalendar();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                EditTripActivity.this,
                now.get(PersianCalendar.HOUR_OF_DAY),
                now.get(PersianCalendar.MINUTE),
                true
        );
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
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


    //show list with search view as dialog
    private void showSearchListDialog(EditText editText, String title, List<Region> regions) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditTripActivity.this);
        builder.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View listViewDialog = inflater.inflate(R.layout.list_with_search_view, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.dialog_toolbar_title);
        Button close = listViewDialog.findViewById(R.id.dialog_toolbar_close);
        ListView listView = listViewDialog.findViewById(R.id.listWithSearchList);
        EditText searchInput = listViewDialog.findViewById(R.id.listWithSearchInput);
        listTitle.setText(title);
        String[] items = extractRegionName(regions);
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


    //get name of transports
    private String[] extractTransportName(List<Transport> transports) {
        String[] names = new String[transports.size()];
        for (int i = 0; i < transports.size(); i++) {
            names[i] = transports.get(i).getName();
        }
        return names;
    }


    //get name of regions
    private String[] extractRegionName(List<Region> regions) {
        String[] names = new String[regions.size()];
        for (int i = 0; i < regions.size(); i++) {
            names[i] = regions.get(i).getTitle();
        }
        return names;
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


    //get city list from DB
    private void getCityList() {
        RegionListRequest request = new RegionListRequest();
        request.setCountryId(0);

        TaskCallBack<Object> regionsListResponse = result -> {
            RegionsListResponse ress = JsonCodec.toObject((Map) result, RegionsListResponse.class);
            List<RegionDto> regionDtoList = ress.getRegionDtoList();
            List<Region> list = new ArrayList<>();
            for (int i = 0; i < regionDtoList.size(); i++) {
                Region region = new Region();
                region.setId(regionDtoList.get(i).getId());
                region.setTitle(regionDtoList.get(i).getName());
                list.add(region);
            }
            setRegion_list(list);
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
            for (int i = 0; i < regionDtoList.size(); i++) {
                Region region = new Region();
                region.setId(regionDtoList.get(i).getId());
                region.setTitle(regionDtoList.get(i).getName());
                list.add(region);
            }
            setCountry_list(list);
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
            for (int i = 0; i < vehicleDtos.size(); i++) {
                Transport transport = new Transport();
                transport.setId(vehicleDtos.get(i).getId());
                transport.setName(vehicleDtos.get(i).getDescription());
                list.add(transport);
            }
            setTransport_list(list);
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(vehiclesListResponse, this, null, ServiceNames.VEHICLE_LIST, false);
        list.execute();
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditTripActivity.this);
        builder.setMessage(getResources().getString(R.string.exit_message));
        builder.setPositiveButton(getResources().getString(R.string.exit_save_changes),
                (dialogInterface, i) -> {
                    insertTripIntoDB();
                    finish();
                });
        builder.setNeutralButton(getResources().getString(R.string.exit_cancel),
                (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
        builder.setNegativeButton(getResources().getString(R.string.exit_discard),
                (dialogInterface, i) -> {
                    finish();
                });
        builder.show();
    }

    @Override
    public void onResume(){
        super.onResume();

        loadChecklist();
    }

}
