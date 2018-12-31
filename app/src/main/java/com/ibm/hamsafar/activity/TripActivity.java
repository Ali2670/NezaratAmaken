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
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.TripInfo;
import com.ibm.hamsafar.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.SubmitTripRequest;
import hamsafar.ws.response.SubmitTripResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TripActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    private Context context = this;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private TextInputLayout portLayout = null;
    private EditText portText = null;
    private TextInputLayout desLayout = null;
    private EditText desText = null;
    private TextInputLayout startDateLayout = null;
    private EditText startDateText = null;
    private TextInputLayout endDateLayout = null;
    private EditText endDateText = null;
    private TextInputLayout transLayout = null;
    private EditText transText = null;
    private Button save = null;
    private Switch checkList = null;
    private LinearLayout parentLayout = null;
    private String DATE_PICKER_CALLER = "start";
    private TripInfo tripInfo = new TripInfo();


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
        endDateLayout = findViewById(R.id.trip_end_date_layout);
        endDateText = findViewById(R.id.trip_end_date);
        transLayout = findViewById(R.id.trip_transport_layout);
        transText = findViewById(R.id.trip_transport);
        save = findViewById(R.id.trip_save_btn);
        checkList = findViewById(R.id.trip_check_list_switch);
        parentLayout = findViewById(R.id.trip_parent);


        toolbarTitle.setText(getResources().getString(R.string.trip_title));

        toolbarBack.setOnClickListener(view -> onBackPressed());

        clearError();

        portLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(portLayout.getEditText(), portLayout));
        desLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(desLayout.getEditText(), desLayout));
        startDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startDateLayout.getEditText(), startDateLayout));
        endDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(endDateLayout.getEditText(), endDateLayout));
        transLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(transLayout.getEditText(), transLayout));


        initialise();

        portText.setOnClickListener(view -> {
            if (checkInternetConnection()) {
                String title = getResources().getString(R.string.trip_port_title);
                String[] items = getResources().getStringArray(R.array.province_array);
                showSearchListDialog(portText, title, items);
            } else {
                Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        });

        desText.setOnClickListener(view -> {
            if (checkInternetConnection()) {
                String title = getResources().getString(R.string.trip_des_title);
                String[] items = getResources().getStringArray(R.array.country_array);
                showSearchListDialog(desText, title, items);
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


        transText.setOnClickListener(view -> {
            String[] array = getResources().getStringArray(R.array.trip_transpose);
            String title = getResources().getString(R.string.trip_trans_title);
            showListDialog(transText, title, array);
        });


        save.setOnClickListener(view -> {
            saveAction(view);
        });


    }

    private void initialise() {
        //get user location
        portText.setText("تهران");

        //set now for start date
        startDateText.setText(DateUtil.getCurrentDate());

        transText.setText(getResources().getString(R.string.trip_default_trans));

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
                setTripInfo();
                insertTripIntoDB();
                Intent intent;
                if (checkList.isChecked()) {
                    intent = new Intent(TripActivity.this, CheckListActivity.class);
                    intent.putExtra("trip_info", tripInfo);
                } else {
                    intent = new Intent(TripActivity.this, Main.class);
                    Toast.makeText(context, getResources().getString(R.string.trip_save_message), Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                finish();
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


    private void setTripInfo() {
        tripInfo.setPort(portText.getText().toString().trim());
        tripInfo.setDes(desText.getText().toString().trim());
        tripInfo.setStart(startDateText.getText().toString().trim());
        tripInfo.setEnd(endDateText.getText().toString().trim());
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
        request.setSourceId(0);
        request.setDestinationId(1);
        request.setStartDate( startDateText.getText().toString().trim() );
        request.setStartTime( DateUtil.getCurrentTime() );
        request.setEndDate( endDateText.getText().toString().trim() );
        request.setEndTime( DateUtil.getCurrentTime() );
        request.setTripType((byte) 0);
        request.setTripStatus((byte) 0);

        TaskCallBack<Object> submitTripResponse = result -> {
            SubmitTripResponse ress = JsonCodec.toObject((Map) result, SubmitTripResponse.class);

            Toast.makeText(context, ress.getTripId().toString(), Toast.LENGTH_SHORT).show();

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitTripResponse, this, null, ServiceNames.SUBMIT_TRIP, false);
        list.execute(request);
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


    //show list with search view as dialog
    private void showSearchListDialog(EditText editText, String title, String[] items) {
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
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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
