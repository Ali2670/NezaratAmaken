package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.UnEditableCheckListAdapter;
import com.ibm.hamsafar.object.CheckItem;
import com.ibm.hamsafar.object.TripInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/24/2018.
 */
public class EditTripActivity extends Activity implements DatePickerDialog.OnDateSetListener {


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
    private Switch showChecklist = null;
    private Button done = null;
    private Button cancel = null;
    private CardView clCard = null;
    private RecyclerView checklist = null;

    private LinearLayoutManager linearLayoutManager = null;
    private List<CheckItem> listData = null;
    private UnEditableCheckListAdapter adapter = null;

    private String DATE_PICKER_CALLER = "start";
    private TripInfo tripInfo = new TripInfo();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.trip_edit);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        portLayout = findViewById(R.id.trip_edit_port_layout);
        portText = findViewById(R.id.trip_edit_port);
        desLayout = findViewById(R.id.trip_edit_destination_layout);
        desText = findViewById(R.id.trip_edit_destination);
        startDateLayout = findViewById(R.id.trip_edit_start_date_layout);
        startDateText = findViewById(R.id.trip_edit_start_date);
        endDateLayout = findViewById(R.id.trip_edit_end_date_layout);
        endDateText = findViewById(R.id.trip_edit_end_date);
        transLayout = findViewById(R.id.trip_edit_transport_layout);
        transText = findViewById(R.id.trip_edit_transport);
        done = findViewById(R.id.trip_edit_done_btn);
        cancel = findViewById(R.id.trip_edit_cancel_btn);
        showChecklist = findViewById(R.id.trip_edit_check_list_add);
        clCard = findViewById(R.id.trip_edit_checklist_card);
        checklist = findViewById(R.id.trip_edit_checklist);


        toolbarTitle.setText(getResources().getString(R.string.trip_edit_title));

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


        if (getIntent().hasExtra("trip")) {
            tripInfo = (TripInfo) getIntent().getSerializableExtra("trip");
            portText.setText(tripInfo.getPort());
            desText.setText(tripInfo.getDes());
            startDateText.setText(tripInfo.getStart());
            endDateText.setText(tripInfo.getEnd());
            transText.setText(tripInfo.getTrans());
        }


        /*
         *
         * check trip info
         * if it has checklist then show in recyclerView
         * else make switch visible to choose whether to define one or not
         *
         * have checklist -> show card visible and switch gone
         *
         * */

        clCard.setVisibility(View.GONE);
        showChecklist.setOnClickListener(view -> {
            if ( showChecklist.isChecked() ) {
                clCard.setVisibility( View.VISIBLE );
                fillTripChecklist();
            }
            else clCard.setVisibility( View.GONE );
        });

        /*
        * ^^^
        * extra
        * */

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

            if (hasError) {
                Snackbar.make(view, getResources().getString(R.string.Exc_700007), Snackbar.LENGTH_SHORT).show();
            } else {
                if (checkInternetConnection()) {
                    clearError();
                    insertTripIntoDB();
                    if (showChecklist.getVisibility() == View.VISIBLE && showChecklist.isChecked()) {
                        Intent intent = new Intent(EditTripActivity.this, CheckListActivity.class);
                        intent.putExtra("trip_info", getTripInfo());
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(view -> finish());


    }

    //get and show checklist info if exists
    private void fillTripChecklist() {
        linearLayoutManager = new LinearLayoutManager(EditTripActivity.this, LinearLayoutManager.VERTICAL, false);
        checklist.setLayoutManager(linearLayoutManager);
        getCheckList();
        adapter = new UnEditableCheckListAdapter(this, listData);
        checklist.setAdapter(adapter);
    }

    //fill list using checklist info
    private void getCheckList() {
        listData = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            CheckItem checkItem = new CheckItem();
            checkItem.setId(i);
            checkItem.setTopic("topic" + i);
            checkItem.setDate(tripInfo.getStart());

            if (i % 3 == 0)
                checkItem.setTime("10:" + i);
            else
                checkItem.setTime("");

            if (i % 4 == 0)
                checkItem.setChecked(true);
            else
                checkItem.setChecked(false);

            listData.add(checkItem);
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

    private TripInfo getTripInfo() {
        TripInfo trip = new TripInfo();
        trip.setPort(portText.getText().toString().trim());
        trip.setDes(desText.getText().toString().trim());
        trip.setStart(startDateText.getText().toString().trim());
        trip.setEnd(endDateText.getText().toString().trim());
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

}
