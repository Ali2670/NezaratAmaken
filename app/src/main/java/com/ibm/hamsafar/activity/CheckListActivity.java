package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.CheckItemAdapter;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.CheckItem;
import com.ibm.hamsafar.object.TripInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.ChecklistItemDto;
import hamsafar.ws.common.ReminderDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.GetChecklistRequest;
import hamsafar.ws.request.SubmitTripChecklistRequest;
import hamsafar.ws.response.GetChecklistResponse;
import hamsafar.ws.response.SubmitChecklistResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckListActivity extends Activity {

    private Context context = this;
    private RecyclerView recyclerView = null;
    private FloatingActionButton addItem = null;
    private LinearLayoutManager linearLayoutManager = null;
    private CheckItemAdapter adapter = null;
    private List<CheckItem> listData;
    private CoordinatorLayout coordinatorLayout = null;
    private Button done = null;
    private Button cancel = null;
    private TripInfo tripInfo = null;
    private static boolean has_trip = false;

    //trip card
    private LinearLayout tripParent = null;
    private TextView tripCardPort = null;
    private TextView tripCardDestination = null;
    private TextView tripCardStart = null;
    private TextView tripCardStartTime = null;
    private TextView tripCardEnd = null;
    private TextView tripCardEndTime = null;
    private TextView tripCardTrans = null;

    private static Integer trip_id;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        recyclerView = findViewById(R.id.cl_recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        addItem = findViewById(R.id.cl_add_item);
        done = findViewById(R.id.toolbar_save);
        cancel = findViewById(R.id.toolbar_cancel);


        tripParent = findViewById(R.id.trip_card_parent);
        tripCardPort = findViewById(R.id.trip_card_port);
        tripCardDestination = findViewById(R.id.trip_card_destination);
        tripCardStart = findViewById(R.id.trip_card_start_date);
        tripCardStartTime = findViewById(R.id.trip_card_start_time);
        tripCardEnd = findViewById(R.id.trip_card_end_date);
        tripCardEndTime = findViewById(R.id.trip_card_end_time);
        tripCardTrans = findViewById(R.id.trip_card_transport);

        /*
         * set proper date for check items
         * trip date if related to a trip
         * current date if no trip is assigned
         * */
        tripInfo = new TripInfo();
        if (getIntent().hasExtra("trip_info")) {
            tripInfo = (TripInfo) getIntent().getSerializableExtra("trip_info");
            has_trip = true;
            tripCardPort.setText(tripInfo.getPort());
            tripCardDestination.setText(tripInfo.getDes());
            tripCardStart.setText(tripInfo.getStartDate());
            tripCardStartTime.setText(tripInfo.getStartTime());
            tripCardEnd.setText(tripInfo.getEndDate());
            tripCardEndTime.setText(tripInfo.getEndTime());
            tripCardTrans.setText(tripInfo.getTrans());
            trip_id = tripInfo.getId();
        } else {
            has_trip = false;
            tripParent.setVisibility(View.GONE);
        }

        getCheckList();


        addItem.setOnClickListener(view -> {
            Intent intent = new Intent(CheckListActivity.this, CheckListItemEditActivity.class);
            intent.putExtra("trip_id", trip_id );
            startActivity(intent);
        });

        cancel.setOnClickListener(view -> {
            finish();
        });

        done.setOnClickListener(view -> {
            saveChecklistIntoDB();
        });

    }

    //save checklist info into DB
    private void saveChecklistIntoDB() {
        if (trip_id == 0) {
            //save without trip id
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Integer user_id = sharedPreferences.getInt("user_id", 0);

            SubmitTripChecklistRequest request = new SubmitTripChecklistRequest();
            request.setUserId(user_id);

            //fill items
            List<ChecklistItemDto> listDB = new ArrayList<>();
            for (int i = 0; i < listData.size(); i++) {
                ChecklistItemDto checklistItemDto = new ChecklistItemDto();
                checklistItemDto.setUserId(user_id);
                checklistItemDto.setTripId(trip_id);
                checklistItemDto.setTitle(listData.get(i).getTopic());
                checklistItemDto.setFixedTitle(listData.get(i).getTopic() );

                if( listData.get(i).isChecked() )
                    checklistItemDto.setStatus((byte) 1);
                if( !listData.get(i).isChecked() )
                    checklistItemDto.setStatus((byte) 0);

                String date_st = listData.get(i).getDate();
                String time_st = listData.get(i).getTime();
                if( !date_st.equals("") && !time_st.equals("") ) {

                    checklistItemDto.setReminderFlag((byte) 1);

                    //create reminder
                    ReminderDto reminderDto = new ReminderDto();
                    reminderDto.setStartDate(date_st);
                    reminderDto.setStartTime(time_st);
                    reminderDto.setReminderType((byte) 0);
                    reminderDto.setRemindDate(date_st);
                    reminderDto.setRemindTime(time_st);
                    reminderDto.setStatus((byte) 0);

                    checklistItemDto.setReminderDto(reminderDto);
                }
                else {
                    checklistItemDto.setReminderFlag((byte) 0);
                }

                listDB.add(checklistItemDto);

            }

            request.setTripId(trip_id);
            request.setChecklistDtoList( listDB );

            TaskCallBack<Object> submitChecklistResponse = result -> {
                SubmitChecklistResponse ress = JsonCodec.toObject((Map) result, SubmitChecklistResponse.class);

                if (ress.getSuccessful() ) {
                    Toast.makeText(context, getResources().getString(R.string.save_success_message), Toast.LENGTH_SHORT).show();
                    finish();
                }

            };
            AsyncTask<Object, Void, WsResult> list = new ListHttp(submitChecklistResponse, this, null, ServiceNames.SUBMIT_CHECKLIST, false);
            list.execute(request);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Integer user_id = sharedPreferences.getInt("user_id", 0);

            SubmitTripChecklistRequest request = new SubmitTripChecklistRequest();
            request.setUserId(user_id);

            //fill items
            List<ChecklistItemDto> listDB = new ArrayList<>();
            for (int i = 0; i < listData.size(); i++) {
                ChecklistItemDto checklistItemDto = new ChecklistItemDto();
                checklistItemDto.setUserId(user_id);
                checklistItemDto.setTripId(trip_id);
                checklistItemDto.setTitle(listData.get(i).getTopic());
                checklistItemDto.setFixedTitle(listData.get(i).getTopic() );

                if( listData.get(i).isChecked() )
                    checklistItemDto.setStatus((byte) 1);
                if( !listData.get(i).isChecked() )
                    checklistItemDto.setStatus((byte) 0);

                String date_st = listData.get(i).getDate();
                String time_st = listData.get(i).getTime();
                if( !date_st.equals("") && !time_st.equals("") ) {

                    checklistItemDto.setReminderFlag((byte) 1);

                    //create reminder
                    ReminderDto reminderDto = new ReminderDto();
                    reminderDto.setStartDate(date_st);
                    reminderDto.setStartTime(time_st);
                    reminderDto.setReminderType((byte) 0);
                    reminderDto.setRemindDate(date_st);
                    reminderDto.setRemindTime(time_st);
                    reminderDto.setStatus((byte) 0);

                    checklistItemDto.setReminderDto(reminderDto);
                }
                else {
                    checklistItemDto.setReminderFlag((byte) 0);
                }

                listDB.add(checklistItemDto);

            }

            request.setTripId(trip_id);
            request.setChecklistDtoList( listDB );

            TaskCallBack<Object> submitChecklistResponse = result -> {
                SubmitChecklistResponse ress = JsonCodec.toObject((Map) result, SubmitChecklistResponse.class);

                if (ress.getSuccessful() ) {
                    Toast.makeText(context, getResources().getString(R.string.save_success_message), Toast.LENGTH_SHORT).show();
                    finish();
                }

            };
            AsyncTask<Object, Void, WsResult> list = new ListHttp(submitChecklistResponse, this, null, ServiceNames.SUBMIT_CHECKLIST, false);
            list.execute(request);
        }
    }

    //download checklist items
    private void getCheckList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        GetChecklistRequest request = new GetChecklistRequest();
        request.setUserId(user_id);
        //if( has_trip )
        //if( trip_id != 0 )
        if ( trip_id != null)
            request.setTripId(trip_id);

        TaskCallBack<Object> getChecklistResponse = result -> {
            GetChecklistResponse ress = JsonCodec.toObject((Map) result, GetChecklistResponse.class);
            List<ChecklistItemDto> listDB;
            listDB = ress.getChecklistItemDtoList();


            //if (ress.getChecklistItemDtoList().size() != 0 ) {
                setListData(listDB);
                linearLayoutManager = new LinearLayoutManager(CheckListActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new CheckItemAdapter(this, listData);
                recyclerView.setAdapter(adapter);
            //}

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(getChecklistResponse, this, null, ServiceNames.GET_CHECKLIST, false);
        list.execute(request);
    }


    //fill list using checklist info
    private void setListData(List<ChecklistItemDto> listDB) {
        listData = new ArrayList<>();
        for (int i = 0; i < listDB.size(); i++) {
            CheckItem checkItem = new CheckItem();
            checkItem.setId(listDB.get(i).getId());
            checkItem.setTopic(listDB.get(i).getTitle());

            if (listDB.get(i).getReminderDto() != null) {
                checkItem.setDate(listDB.get(i).getReminderDto().getRemindDate());
                checkItem.setTime(listDB.get(i).getReminderDto().getRemindTime());
            }
            if (listDB.get(i).getReminderDto() == null) {
                checkItem.setDate("");
                checkItem.setTime("");
            }

            if (listDB.get(i).getStatus() == 1)
                checkItem.setChecked(true);
            if (listDB.get(i).getStatus() == 0)
                checkItem.setChecked(false);

            listData.add(checkItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getCheckList();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListActivity.this);
        builder.setMessage(getResources().getString(R.string.exit_message));
        builder.setPositiveButton(getResources().getString(R.string.exit_save_changes),
                (dialogInterface, i) -> {
                    saveChecklistIntoDB();
                    finish();
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
